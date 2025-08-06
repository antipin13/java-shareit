package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
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

            return bookingMapper.toBookingDto(newBooking);
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

        if (approved == true) {
            bookingOwner.setStatus(BookingStatus.APPROVED);
            return bookingMapper.toBookingDto(bookingOwner);
        } else {
            bookingOwner.setStatus(BookingStatus.REJECTED);
            return bookingMapper.toBookingDto(bookingOwner);
        }
    }

    public BookingDto getBookingByBookerIdOrOwnerId(Long bookingId, Long userId) {
        Booking booking = getBookingOrThrow(bookingId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, NOT_FOUND_MESSAGE_USER));

        Optional<Booking> bookingBooker = bookingRepository.findByIdAndBookerId(bookingId, userId);

        Optional<Booking> bookingOwner = bookingRepository.findByIdAndOwnerId(bookingId, userId);

        if (bookingBooker.isPresent()) {
            return bookingMapper.toBookingDto(bookingBooker.get());
        } else if (bookingOwner.isPresent()) {
            return bookingMapper.toBookingDto(bookingOwner.get());
        } else {
            throw  new NotFoundException(String.format("Бронирование с ID - %d не найдено", bookingId));
        }
    }

    public List<BookingDto> getBookingByBooker(Long bookerId) {
        getUserOrThrow(bookerId);

        return bookingRepository.findByBookerId(bookerId).stream()
                .map(bookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_MESSAGE_USER, userId)));
    }

    private Item getItemOrThrow(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_MESSAGE_ITEM, itemId)));
    }

    private Booking getBookingOrThrow(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_MESSAGE_BOOKING, bookingId)));
    }
}
