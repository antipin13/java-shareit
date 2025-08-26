package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItems;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemRequestServiceImplTest {
    @Mock
    UserRepository userRepository;

    @Mock
    ItemRepository itemRepository;

    @Mock
    ItemRequestRepository itemRequestRepository;

    @Mock
    ItemRequestMapper itemRequestMapper;

    @InjectMocks
    ItemRequestServiceImpl itemRequestService;

    @Mock
    User user;

    @BeforeEach
    void setUp() {
        user = new User();
    }

    @Test
    void saveRequest_Success() {
        Long userId = 1L;
        NewItemRequest newRequest = new NewItemRequest();

        ItemRequest itemRequest = new ItemRequest();
        ItemRequest savedRequest = new ItemRequest();

        ItemRequestDto expectedDto = new ItemRequestDto();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        when(itemRequestMapper.toItemRequest(newRequest)).thenReturn(itemRequest);

        when(itemRequestRepository.save(any())).thenReturn(savedRequest);

        when(itemRequestMapper.toItemRequestDto(savedRequest)).thenReturn(expectedDto);

        ItemRequestDto result = itemRequestService.saveRequest(userId, newRequest);

        assertNotNull(result);
        assertEquals(expectedDto, result);
        verify(itemRequestMapper).toItemRequest(newRequest);
        verify(itemRequestRepository).save(any());
        verify(itemRequestMapper).toItemRequestDto(savedRequest);
    }

    @Test
    void findItemRequestsByRequestorId() {
        Long requestorId = 1L;

        when(userRepository.findById(requestorId)).thenReturn(Optional.of(user));

        ItemRequest request1 = new ItemRequest();
        ItemRequest request2 = new ItemRequest();

        List<ItemRequest> itemRequests = Arrays.asList(request1, request2);
        when(itemRequestRepository.findByRequestorIdOrderByCreatedDesc(requestorId))
                .thenReturn(itemRequests);

        ItemRequestDto dto1 = new ItemRequestDto();
        ItemRequestDto dto2 = new ItemRequestDto();

        when(itemRequestMapper.toItemRequestDto(request1)).thenReturn(dto1);
        when(itemRequestMapper.toItemRequestDto(request2)).thenReturn(dto2);

        List<ItemRequestDto> result = itemRequestService.findItemRequestsByRequestorId(requestorId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(dto1, result.get(0));
        assertEquals(dto2, result.get(1));

        verify(itemRequestRepository).findByRequestorIdOrderByCreatedDesc(requestorId);
        verify(itemRequestMapper).toItemRequestDto(request1);
        verify(itemRequestMapper).toItemRequestDto(request2);
    }

    @Test
    void findItemRequestsOtherUsers() {
        Long requestorId = 1L;

        when(userRepository.findById(requestorId)).thenReturn(Optional.of(user));

        ItemRequest request1 = new ItemRequest();
        ItemRequest request2 = new ItemRequest();

        List<ItemRequest> itemRequests = Arrays.asList(request1, request2);
        when(itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(requestorId))
                .thenReturn(itemRequests);

        ItemRequestDto dto1 = new ItemRequestDto();
        ItemRequestDto dto2 = new ItemRequestDto();

        when(itemRequestMapper.toItemRequestDto(request1)).thenReturn(dto1);
        when(itemRequestMapper.toItemRequestDto(request2)).thenReturn(dto2);

        List<ItemRequestDto> result = itemRequestService.findItemRequestsOtherUsers(requestorId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(dto1, result.get(0));
        assertEquals(dto2, result.get(1));

        verify(itemRequestRepository).findByRequestorIdNotOrderByCreatedDesc(requestorId);
        verify(itemRequestMapper).toItemRequestDto(request1);
        verify(itemRequestMapper).toItemRequestDto(request2);
    }

    @Test
    void findById_ReturnsItemRequestDtoWithItems() {
        Long requestId = 1L;

        ItemRequestDtoWithItems dtoWithItems = new ItemRequestDtoWithItems();

        when(itemRequestRepository.findById(requestId))
                .thenReturn(Optional.of(new ItemRequest()));
        when(itemRequestMapper.toItemRequestDtoWithItems(any()))
                .thenReturn(dtoWithItems);

        Item item1 = new Item();
        Item item2 = new Item();
        List<Item> items = Arrays.asList(item1, item2);

        when(itemRepository.findByRequestId(requestId)).thenReturn(items);

        ItemRequestDtoWithItems result = itemRequestService.findById(requestId);

        assertNotNull(result);
        assertEquals(dtoWithItems, result);
        assertEquals(items, result.getItems());

        verify(itemRequestRepository).findById(requestId);
        verify(itemRequestMapper).toItemRequestDtoWithItems(any());
        verify(itemRepository).findByRequestId(requestId);
    }
}