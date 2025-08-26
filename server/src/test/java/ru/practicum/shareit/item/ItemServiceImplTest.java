package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserServiceImpl;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemServiceImplTest {
    @Mock
    private Item item;

    @Mock
    private User user;

    @Mock
    private Comment comment;

    @Mock
    private NewCommentRequest request;

    @Mock
    private CommentDto commentDto;

    @Mock
    ItemRepository itemRepository;

    @Mock
    CommentRepository commentRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    UserServiceImpl userService;

    @Mock
    BookingRepository bookingRepository;

    @Mock
    ItemMapper itemMapper;

    @Mock
    CommentMapper commentMapper;

    @Mock
    ItemRequestRepository itemRequestRepository;

    @Spy
    @InjectMocks
    ItemServiceImpl itemService;

    @BeforeEach
    void setUp() {
        item = new Item();
        user = new User();
    }

    @Test
    void saveItem() {
        Long userId = 1L;
        Long requestId = 10L;

        NewItemRequest request = new NewItemRequest();
        request.setName("Item Name");
        request.setDescription("Item Description");
        request.setAvailable(true);
        request.setRequestId(requestId);

        User user = new User();
        user.setId(userId);

        Item item = new Item();
        item.setId(100L);
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setAvailable(request.getAvailable());

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(requestId);

        Item savedItem = new Item();
        savedItem.setId(100L);
        savedItem.setName(request.getName());
        savedItem.setDescription(request.getDescription());
        savedItem.setAvailable(request.getAvailable());
        savedItem.setOwner(user);
        savedItem.setRequest(itemRequest);

        ItemDto itemDto = new ItemDto();

        doReturn(user).when(itemService).getUserOrThrow(userId);

        when(itemMapper.toItem(request)).thenReturn(item);

        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));

        when(itemRepository.save(any(Item.class))).thenReturn(savedItem);

        when(itemMapper.toItemDto(savedItem)).thenReturn(itemDto);

        ItemDto result = itemService.saveItem(userId, request);

        assertNotNull(result);
        assertEquals(itemDto, result);

        verify(itemService).getUserOrThrow(userId);
        verify(itemMapper).toItem(request);
        verify(itemRequestRepository).findById(requestId);
        verify(itemRepository).save(any(Item.class));
        verify(itemMapper).toItemDto(savedItem);
    }

    @Test
    void saveItemWhenRequestIdIsNull() {
        Long userId = 1L;

        NewItemRequest request = new NewItemRequest();
        request.setName("Item Name");
        request.setDescription("Item Description");
        request.setAvailable(true);
        request.setRequestId(null);

        User user = new User();

        Item item = new Item();
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setAvailable(request.getAvailable());

        Item savedItem = new Item();
        savedItem.setId(101L);
        savedItem.setName(request.getName());
        savedItem.setDescription(request.getDescription());
        savedItem.setAvailable(request.getAvailable());
        savedItem.setOwner(user);

        ItemDto itemDto = new ItemDto();

        doReturn(user).when(itemService).getUserOrThrow(userId);
        when(itemMapper.toItem(request)).thenReturn(item);
        when(itemRepository.save(any(Item.class))).thenReturn(savedItem);
        when(itemMapper.toItemDto(savedItem)).thenReturn(itemDto);

        ItemDto result = itemService.saveItem(userId, request);

        assertNotNull(result);
        assertEquals(itemDto, result);
        verify(itemRequestRepository, never()).findById(anyLong());
    }

    @Test
    void updateItem() {
        Long userId = 1L;
        Long itemId = 10L;

        UpdateItemRequest request = new UpdateItemRequest();
        request.setName("Updated Name");
        request.setDescription("Updated Description");
        request.setAvailable(false);

        User user = new User();
        user.setId(userId);

        Item existingItem = new Item();
        existingItem.setId(itemId);
        existingItem.setName("Old Name");
        existingItem.setDescription("Old Description");
        existingItem.setAvailable(true);
        existingItem.setOwner(user);

        Item updatedItem = new Item();
        updatedItem.setId(itemId);
        updatedItem.setName(request.getName());
        updatedItem.setDescription(request.getDescription());
        updatedItem.setAvailable(request.getAvailable());
        updatedItem.setOwner(user);

        ItemDto itemDto = new ItemDto();

        doReturn(user).when(itemService).getUserOrThrow(userId);
        doReturn(existingItem).when(itemService).getItemOrThrow(itemId);

        when(itemMapper.updateItemFields(existingItem, request)).thenReturn(updatedItem);
        when(itemRepository.save(updatedItem)).thenReturn(updatedItem);
        when(itemMapper.toItemDto(updatedItem)).thenReturn(itemDto);

        ItemDto result = itemService.updateItem(userId, request, itemId);

        assertNotNull(result);
        assertEquals(itemDto, result);

        verify(itemService).getUserOrThrow(userId);
        verify(itemService).getItemOrThrow(itemId);
        verify(itemMapper).updateItemFields(existingItem, request);
        verify(itemRepository).save(updatedItem);
        verify(itemMapper).toItemDto(updatedItem);
    }

    @Test
    void findItemById() {
        Long userId = 1L;
        Long itemId = 10L;

        User user = new User();
        user.setId(userId);

        Item item = new Item();
        item.setId(itemId);

        CommentDto commentDto = new CommentDto();
        List<CommentDto> comments = Arrays.asList(commentDto);

        ItemWithCommentsDto itemWithCommentsDto = new ItemWithCommentsDto();

        doReturn(user).when(itemService).getUserOrThrow(userId);
        doReturn(item).when(itemService).getItemOrThrow(itemId);

        when(commentRepository.findByItemId(itemId)).thenReturn(Collections.singletonList(new Comment()));
        when(commentMapper.toCommentDto(any(Comment.class))).thenReturn(commentDto);

        when(itemMapper.toItemWithCommentsDto(item)).thenReturn(itemWithCommentsDto);

        ItemWithCommentsDto result = itemService.findItemById(userId, itemId);

        assertNotNull(result);
        assertEquals(itemWithCommentsDto, result);
        assertEquals(comments, result.getComments());

        verify(itemService).getUserOrThrow(userId);
        verify(itemService).getItemOrThrow(itemId);
        verify(commentRepository).findByItemId(itemId);
        verify(commentMapper, atLeastOnce()).toCommentDto(any(Comment.class));
        verify(itemMapper).toItemWithCommentsDto(item);
    }

    @Test
    void findItemsByUserId_ShouldReturnListOfItemWithDateDto() {
        Long userId = 1L;

        User user = new User();
        user.setId(userId);

        Item item1 = new Item();
        item1.setId(101L);
        Item item2 = new Item();
        item2.setId(102L);
        List<Item> items = Arrays.asList(item1, item2);

        Booking booking1 = new Booking();
        booking1.setEnd(LocalDateTime.of(2023, 10, 10, 10, 0));
        booking1.setStart(LocalDateTime.of(2023, 10, 1, 10, 0));
        booking1.setItem(item1);

        Booking booking2 = new Booking();
        booking2.setEnd(LocalDateTime.of(2023, 12, 12, 12, 0));
        booking2.setStart(LocalDateTime.of(2023, 12, 1, 12, 0));
        booking2.setItem(item2);

        ItemWithDateDto dto1 = new ItemWithDateDto();
        ItemWithDateDto dto2 = new ItemWithDateDto();

        doReturn(user).when(itemService).getUserOrThrow(userId);
        when(itemRepository.findByOwnerId(userId)).thenReturn(items);
        when(bookingRepository.findByItemId(101L)).thenReturn(Arrays.asList(booking1));
        when(bookingRepository.findByItemId(102L)).thenReturn(Arrays.asList(booking2));
        when(itemMapper.toItemWithDateDto(item1)).thenReturn(dto1);
        when(itemMapper.toItemWithDateDto(item2)).thenReturn(dto2);

        List<ItemWithDateDto> result = itemService.findItemsByUserId(userId);

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(dto1, result.get(0));
        assertEquals(dto2, result.get(1));
        assertEquals(booking1.getEnd(), result.get(0).getLastBookingDate());
        assertEquals(booking2.getStart(), result.get(1).getNextBookingDate());

        verify(itemService).getUserOrThrow(userId);
        verify(itemRepository).findByOwnerId(userId);
        verify(bookingRepository, times(1)).findByItemId(101L);
        verify(bookingRepository, times(1)).findByItemId(102L);
        verify(itemMapper, times(2)).toItemWithDateDto(any());
    }

    @Test
    void findItemsByTextAndAvailable() {
        Long userId = 1L;
        String text = "item";

        doReturn(new User()).when(itemService).getUserOrThrow(userId);

        Item item1 = new Item();
        Item item2 = new Item();

        ItemDto itemDto1 = new ItemDto();
        ItemDto itemDto2 = new ItemDto();

        when(itemRepository.findByText(text)).thenReturn(Arrays.asList(item1, item2));
        when(itemMapper.toItemDto(item1)).thenReturn(itemDto1);
        when(itemMapper.toItemDto(item2)).thenReturn(itemDto2);

        List<ItemDto> result = itemService.findItemsByTextAndAvailable(userId, text);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(itemDto1));
        assertTrue(result.contains(itemDto2));

        verify(itemService).getUserOrThrow(userId);
        verify(itemRepository).findByText(text);
        verify(itemMapper, times(2)).toItemDto(any());
    }

    @Test
    void findItemsByTextAndAvailableReturnEmptyList() {
        Long userId = 1L;
        doReturn(new User()).when(itemService).getUserOrThrow(userId);

        List<ItemDto> resultNull = itemService.findItemsByTextAndAvailable(userId, null);
        List<ItemDto> resultBlank = itemService.findItemsByTextAndAvailable(userId, "  ");

        assertTrue(resultNull.isEmpty());
        assertTrue(resultBlank.isEmpty());

        verify(itemService, times(2)).getUserOrThrow(userId);
        verifyNoInteractions(itemRepository);
    }

    @Test
    void saveComment() {
        Long itemId = 1L;
        Long userId = 2L;

        Booking booking = mock(Booking.class);
        when(bookingRepository.findByItemIdAndBookerId(itemId, userId)).thenReturn(Optional.of(booking));
        when(booking.getEnd()).thenReturn(LocalDateTime.now().minusDays(1));

        doReturn(item).when(itemService).getItemOrThrow(itemId);
        doReturn(user).when(itemService).getUserOrThrow(userId);

        when(commentMapper.toComment(request)).thenReturn(comment);
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentMapper.toCommentDto(comment)).thenReturn(commentDto);

        CommentDto result = itemService.saveComment(itemId, userId, request);

        assertNotNull(result);
        assertEquals(commentDto, result);
        verify(bookingRepository).findByItemIdAndBookerId(itemId, userId);
        verify(itemService).getItemOrThrow(itemId);
        verify(itemService).getUserOrThrow(userId);
        verify(commentMapper).toComment(request);
        verify(commentRepository).save(comment);
        verify(commentMapper).toCommentDto(comment);
    }

    @Test
    void saveCommentBookingNotFound() {
        Long itemId = 1L;
        Long userId = 2L;

        when(bookingRepository.findByItemIdAndBookerId(itemId, userId))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                itemService.saveComment(itemId, userId, request));

        verify(bookingRepository).findByItemIdAndBookerId(itemId, userId);
    }

    @Test
    void saveCommentBookingNotPast() {
        Long itemId = 1L;
        Long userId = 2L;

        doReturn(item).when(itemService).getItemOrThrow(itemId);
        doReturn(user).when(itemService).getUserOrThrow(userId);

        Booking booking = mock(Booking.class);
        when(bookingRepository.findByItemIdAndBookerId(itemId, userId))
                .thenReturn(Optional.of(booking));
        when(booking.getEnd()).thenReturn(LocalDateTime.now().plusDays(1));

        assertThrows(ResponseStatusException.class, () ->
                itemService.saveComment(itemId, userId, request));

        verify(bookingRepository).findByItemIdAndBookerId(itemId, userId);
    }

    @Test
    void getUserOrThrow() {
        Long userId = 1L;
        User user = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User result = itemService.getUserOrThrow(userId);

        assertNotNull(result);
        assertEquals(user, result);
        verify(userRepository).findById(userId);
    }

    @Test
    void getUserOrThrowUserNotFound() {
        Long userId = 2L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getUserOrThrow(userId));

        verify(userRepository).findById(userId);
    }

    @Test
    void getItemOrThrow() {
        Long itemId = 10L;
        Item item = new Item();
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        Item result = itemService.getItemOrThrow(itemId);

        assertNotNull(result);
        assertEquals(item, result);
        verify(itemRepository).findById(itemId);
    }

    @Test
    void getItemOrThrowItemNotFound() {
        Long itemId = 20L;
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getItemOrThrow(itemId));

        verify(itemRepository).findById(itemId);
    }
}