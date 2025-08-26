package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {
    final ItemRepository itemRepository;
    final UserRepository userRepository;
    final BookingRepository bookingRepository;
    final BookingMapper bookingMapper;
    final UserMapper userMapper;
    final ItemMapper itemMapper;
    static final String NOT_FOUND_MESSAGE_ITEM = "Предмет с ID - %d не найден";
    static final String NOT_FOUND_MESSAGE_USER = "Пользователь с ID - %d не найден";
    static final String NOT_FOUND_MESSAGE_BOOKING = "Бронирование с ID - %d не найдено";

    @Override
    public BookingDto saveBooking(Long userId, NewBookingRequest request) {
        User user = getUserOrThrow(userId);

        Item item = getItemOrThrow(request.getItemId());

        Booking newBooking = bookingMapper.toBooking(request);
        newBooking.setBooker(user);

        if (newBooking.getStart().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Дата начала бронирования не может быть в прошлом");
        }
        if (newBooking.getEnd().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Дата окончания бронирования не может быть в прошлом");
        }
        if (newBooking.getEnd().equals(newBooking.getStart())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Даты начала и окончания бронирования не могут быть равны");
        }

        if (item.getAvailable() == true) {
            newBooking.setStatus(BookingStatus.WAITING);
            newBooking = bookingRepository.save(newBooking);

            BookingDto bookingDto = bookingMapper.toBookingDto(newBooking);
            bookingDto.setItem(itemMapper.toItemBookingDto(item));
            bookingDto.setBooker(userMapper.toUserBookingDto(user));

            return bookingDto;
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Вещь недоступна для бронирования");
        }
    }

    @Override
    public BookingDto approveBooking(Long bookingId, Long userId, Boolean approved) {
        Booking booking = getBookingOrThrow(bookingId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, NOT_FOUND_MESSAGE_USER));

        Booking bookingOwner = bookingRepository.findByIdAndOwnerId(bookingId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        String.format("Пользователь с ID - %d не владелец вещи", userId)));

        Item item = getItemOrThrow(bookingOwner.getItem().getId());

        if (approved == true) {
            bookingOwner.setStatus(BookingStatus.APPROVED);
            BookingDto bookingDto = bookingMapper.toBookingDto(bookingOwner);
            bookingDto.setItem(itemMapper.toItemBookingDto(item));
            bookingDto.setBooker(userMapper.toUserBookingDto(booking.getBooker()));

            return bookingDto;
        } else {
            bookingOwner.setStatus(BookingStatus.REJECTED);
            return bookingMapper.toBookingDto(bookingOwner);
        }
    }

    public BookingDto getBookingByBookerIdOrOwnerId(Long bookingId, Long userId) {
        Booking booking = getBookingOrThrow(bookingId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, NOT_FOUND_MESSAGE_USER));

        Item item = getItemOrThrow(booking.getItem().getId());

        Optional<Booking> bookingBooker = bookingRepository.findByIdAndBookerId(bookingId, userId);

        Optional<Booking> bookingOwner = bookingRepository.findByIdAndOwnerId(bookingId, userId);

        if (bookingBooker.isPresent()) {
            BookingDto bookingDto = bookingMapper.toBookingDto(bookingBooker.get());
            bookingDto.setItem(itemMapper.toItemBookingDto(item));
            bookingDto.setBooker(userMapper.toUserBookingDto(booking.getBooker()));

            return bookingDto;
        } else if (bookingOwner.isPresent()) {
            BookingDto bookingDto = bookingMapper.toBookingDto(bookingOwner.get());
            bookingDto.setItem(itemMapper.toItemBookingDto(item));
            bookingDto.setBooker(userMapper.toUserBookingDto(booking.getBooker()));

            return bookingDto;
        } else {
            throw  new NotFoundException(String.format("Бронирование с ID - %d не найдено", bookingId));
        }
    }

    public List<BookingDto> getBookingByBooker(Long bookerId) {
        getUserOrThrow(bookerId);

        return bookingRepository.findByBookerId(bookerId).stream()
                .map(booking -> {
                    BookingDto dto = bookingMapper.toBookingDto(booking);
                    dto.setItem(itemMapper.toItemBookingDto(booking.getItem()));
                    dto.setBooker(userMapper.toUserBookingDto(booking.getBooker()));
                    return dto;
                }).collect(Collectors.toList());
    }

    public User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_MESSAGE_USER, userId)));
    }

    Item getItemOrThrow(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_MESSAGE_ITEM, itemId)));
    }

    Booking getBookingOrThrow(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_MESSAGE_BOOKING, bookingId)));
    }
}
