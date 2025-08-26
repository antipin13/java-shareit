package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemBookingDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserBookingDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private BookingMapper bookingMapper;
    @Mock
    private ItemMapper itemMapper;
    @Mock
    private UserMapper userMapper;

    @Spy
    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private NewBookingRequest request;

    @Mock
    private User user;

    @Mock
    private Item item;

    private Booking booking;
    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        item = new Item();
        user = new User();
        booking = new Booking();
    }

    @Test
    void saveBooking() {
        Long userId = 1L;
        Long itemId = 2L;

        request = new NewBookingRequest();
        request.setItemId(itemId);
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));

        item.setAvailable(true);
        booking.setStart(request.getStart());
        booking.setEnd(request.getEnd());
        booking.setStatus(BookingStatus.APPROVED);
        booking.setBooker(user);
        booking.setItem(item);
        bookingDto = new BookingDto();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingMapper.toBooking(request)).thenReturn(booking);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.toBookingDto(booking)).thenReturn(bookingDto);
        when(itemMapper.toItemBookingDto(item)).thenReturn(new ItemBookingDto());
        when(userMapper.toUserBookingDto(user)).thenReturn(new UserBookingDto());

        BookingDto result = bookingService.saveBooking(userId, request);

        assertNotNull(result);
        verify(userRepository).findById(userId);
        verify(itemRepository).findById(itemId);
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void saveBookingStartInPast() {
        Long userId = 1L;
        Long itemId = 2L;
        request = new NewBookingRequest();
        request.setItemId(itemId);
        request.setStart(LocalDateTime.now().minusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(1));

        booking.setStart(request.getStart());
        booking.setEnd(request.getEnd());
        booking.setStatus(BookingStatus.APPROVED);
        booking.setBooker(user);
        booking.setItem(item);
        bookingDto = new BookingDto();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingMapper.toBooking(request)).thenReturn(booking);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                bookingService.saveBooking(userId, request));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Дата начала бронирования не может быть в прошлом"));

        verify(userRepository).findById(userId);
        verify(itemRepository).findById(itemId);
    }

    @Test
    void saveBookingEndInPast() {
        Long userId = 1L;
        Long itemId = 2L;
        request = new NewBookingRequest();
        request.setItemId(itemId);
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().minusDays(1));

        booking.setStart(request.getStart());
        booking.setEnd(request.getEnd());
        booking.setStatus(BookingStatus.APPROVED);
        booking.setBooker(user);
        booking.setItem(item);
        bookingDto = new BookingDto();

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(new Item()));
        when(bookingMapper.toBooking(request)).thenReturn(booking);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                bookingService.saveBooking(userId, request));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Дата окончания бронирования не может быть в прошлом"));
    }

    @Test
    void saveBookingStartEqualsEnd() {
        Long userId = 1L;
        Long itemId = 2L;
        LocalDateTime now = LocalDateTime.now();
        request = new NewBookingRequest();
        request.setItemId(itemId);
        request.setStart(now.plusDays(1));
        request.setEnd(now.plusDays(1));

        booking.setStart(request.getStart());
        booking.setEnd(request.getEnd());
        booking.setStatus(BookingStatus.APPROVED);
        booking.setBooker(user);
        booking.setItem(item);
        bookingDto = new BookingDto();

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(new Item()));
        when(bookingMapper.toBooking(request)).thenReturn(booking);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                bookingService.saveBooking(userId, request));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Даты начала и окончания бронирования не могут быть равны"));
    }

    @Test
    void saveBookingItemNotAvailable() {
        Long userId = 1L;
        Long itemId = 2L;
        request = new NewBookingRequest();
        request.setItemId(itemId);
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));

        booking.setStart(request.getStart());
        booking.setEnd(request.getEnd());
        booking.setStatus(BookingStatus.APPROVED);
        booking.setBooker(user);
        booking.setItem(item);
        bookingDto = new BookingDto();

        item.setAvailable(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingMapper.toBooking(request)).thenReturn(booking);

        assertThrows(ResponseStatusException.class, () ->
                bookingService.saveBooking(userId, request));
    }

    @Test
    void approveBooking() {
        Long bookingId = 1L;
        Long userId = 10L;
        booking.setId(bookingId);
        User booker = new User();
        booking.setBooker(booker);
        item.setId(5L);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        user = new User();
        this.item = new Item();
        this.item.setId(5L);

        doReturn(item).when(bookingService).getItemOrThrow(item.getId());
        doReturn(booking).when(bookingService).getBookingOrThrow(bookingId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findByIdAndOwnerId(bookingId, userId)).thenReturn(Optional.of(booking));
        when(bookingMapper.toBookingDto(booking)).thenReturn(new BookingDto());
        when(itemMapper.toItemBookingDto(this.item)).thenReturn(new ItemBookingDto());
        when(userMapper.toUserBookingDto(booker)).thenReturn(new UserBookingDto());

        BookingDto result = bookingService.approveBooking(bookingId, userId, true);

        assertNotNull(result);
        verify(bookingRepository).findByIdAndOwnerId(bookingId, userId);
        assertEquals(BookingStatus.APPROVED, booking.getStatus());
    }

    @Test
    void approveBookingReject() {
        Long bookingId = 2L;
        Long userId = 20L;
        booking.setId(bookingId);
        User booker = new User();
        booking.setBooker(booker);
        item.setId(10L);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        doReturn(item).when(bookingService).getItemOrThrow(item.getId());
        doReturn(booking).when(bookingService).getBookingOrThrow(bookingId);
        User owner = new User();

        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(bookingRepository.findByIdAndOwnerId(bookingId, userId)).thenReturn(Optional.of(booking));
        when(bookingMapper.toBookingDto(booking)).thenReturn(new BookingDto());

        BookingDto result = bookingService.approveBooking(bookingId, userId, false);

        assertNotNull(result);
        verify(bookingRepository).findByIdAndOwnerId(bookingId, userId);
        assertEquals(BookingStatus.REJECTED, booking.getStatus());
    }

    @Test
    void approveBookingInvalidUserThrows() {
        Long bookingId = 3L;
        Long userId = 30L;

        doReturn(new Booking()).when(bookingService).getBookingOrThrow(bookingId);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                bookingService.approveBooking(bookingId, userId, true));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Пользователь с ID - %d не найден"));
    }

    @Test
    void approveBooking_NotOwner_Throws() {
        Long bookingId = 4L;
        Long userId = 40L;
        doReturn(new Booking()).when(bookingService).getBookingOrThrow(bookingId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(bookingRepository.findByIdAndOwnerId(bookingId, userId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                bookingService.approveBooking(bookingId, userId, true));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getReason().contains(
                String.format("Пользователь с ID - %d не владелец вещи", userId)));
    }

    @Test
    void getBookingByBookerIdOrOwnerIdBooker() {
        Long bookingId = 1L;
        Long userId = 10L;

        booking.setId(bookingId);
        User booker = new User();
        booking.setBooker(booker);
        item.setId(5L);
        booking.setItem(item);

        User userEntity = new User();

        doReturn(booking).when(bookingService).getBookingOrThrow(bookingId);
        doReturn(item).when(bookingService).getItemOrThrow(item.getId());

        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(bookingRepository.findByIdAndBookerId(bookingId, userId)).thenReturn(Optional.of(booking));
        when(bookingMapper.toBookingDto(booking)).thenReturn(new BookingDto());
        when(itemMapper.toItemBookingDto(item)).thenReturn(new ItemBookingDto());
        when(userMapper.toUserBookingDto(booker)).thenReturn(new UserBookingDto());

        BookingDto result = bookingService.getBookingByBookerIdOrOwnerId(bookingId, userId);

        assertNotNull(result);
        verify(bookingRepository).findByIdAndBookerId(bookingId, userId);
    }

    @Test
    void getBookingByBookerIdOrOwnerIdOwner() {
        Long bookingId = 2L;
        Long userId = 20L;

        Booking booking = new Booking();
        booking.setId(bookingId);
        User booker = new User();
        booking.setBooker(booker);
        item.setId(10L);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        User userEntity = new User();

        doReturn(booking).when(bookingService).getBookingOrThrow(bookingId);
        doReturn(item).when(bookingService).getItemOrThrow(item.getId());

        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(bookingRepository.findByIdAndBookerId(bookingId, userId)).thenReturn(Optional.empty());
        when(bookingRepository.findByIdAndOwnerId(bookingId, userId)).thenReturn(Optional.of(booking));
        when(bookingMapper.toBookingDto(booking)).thenReturn(new BookingDto());
        when(itemMapper.toItemBookingDto(item)).thenReturn(new ItemBookingDto());
        when(userMapper.toUserBookingDto(booker)).thenReturn(new UserBookingDto());

        BookingDto result = bookingService.getBookingByBookerIdOrOwnerId(bookingId, userId);

        assertNotNull(result);
        verify(bookingRepository).findByIdAndOwnerId(bookingId, userId);
    }

    @Test
    void getBookingByBookerIdOrOwnerIdNotFound() {
        Long bookingId = 2L;
        Long userId = 20L;

        Booking booking = new Booking();
        booking.setId(bookingId);
        User booker = new User();
        booking.setBooker(booker);
        item.setId(10L);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        doReturn(booking).when(bookingService).getBookingOrThrow(bookingId);
        doReturn(item).when(bookingService).getItemOrThrow(item.getId());

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(bookingRepository.findByIdAndBookerId(bookingId, userId)).thenReturn(Optional.empty());
        when(bookingRepository.findByIdAndOwnerId(bookingId, userId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                bookingService.getBookingByBookerIdOrOwnerId(bookingId, userId));
        assertEquals(String.format("Бронирование с ID - %d не найдено", bookingId), exception.getMessage());
    }

    @Test
    void getBookingByBooker() {
        Long bookerId = 1L;

        Booking booking1 = new Booking();
        Booking booking2 = new Booking();
        List<Booking> bookings = Arrays.asList(booking1, booking2);

        when(userRepository.findById(bookerId)).thenReturn(Optional.of(new User()));
        when(bookingRepository.findByBookerId(bookerId)).thenReturn(bookings);

        BookingDto dto1 = new BookingDto();
        BookingDto dto2 = new BookingDto();

        when(bookingMapper.toBookingDto(booking1)).thenReturn(dto1);
        when(bookingMapper.toBookingDto(booking2)).thenReturn(dto2);

        ItemBookingDto itemDto1 = new ItemBookingDto();
        ItemBookingDto itemDto2 = new ItemBookingDto();

        when(itemMapper.toItemBookingDto(any())).thenReturn(itemDto1).thenReturn(itemDto2);

        UserBookingDto userDto1 = new UserBookingDto();
        UserBookingDto userDto2 = new UserBookingDto();

        when(userMapper.toUserBookingDto(any())).thenReturn(userDto1).thenReturn(userDto2);

        List<BookingDto> resultList = bookingService.getBookingByBooker(bookerId);

        assertNotNull(resultList);
        assertEquals(2, resultList.size());

        assertEquals(dto1, resultList.get(0));
        assertEquals(dto2, resultList.get(1));

        verify(bookingMapper, times(2)).toBookingDto(any());
        verify(itemMapper, times(2)).toItemBookingDto(any());
        verify(userMapper, times(2)).toUserBookingDto(any());
    }

    @Test
    void getBookingOrThrow() {
        Long bookingId = 1L;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        Booking result = bookingService.getBookingOrThrow(bookingId);

        assertNotNull(result);
        assertEquals(booking, result);
        verify(bookingRepository).findById(bookingId);
    }

    @Test
    void getBookingOrThrow_ThrowsNotFoundException() {
        Long bookingId = 2L;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            bookingService.getBookingOrThrow(bookingId);
        });

        assertTrue(exception.getMessage().contains(String.format("Бронирование с ID - %d не найдено", bookingId)));
        verify(bookingRepository).findById(bookingId);
    }
}