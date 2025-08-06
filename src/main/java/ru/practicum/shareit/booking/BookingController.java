package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Validated
public class BookingController {
    final BookingServiceImpl bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto create(@RequestHeader(value = "X-Sharer-User-Id", required = false)
                                 @NotNull(message = "ID пользователя должно быть указано в заголовке") Long userId,
                             @Valid @RequestBody NewBookingRequest request) {
        log.info("Запрос на добавление бронирования {}", request);
            return bookingService.saveBooking(userId, request);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDto approveBooking(@RequestHeader(value = "X-Sharer-User-Id", required = false)
                                         @NotNull(message = "ID пользователя должно быть указано в заголовке") Long userId,
                                     @PathVariable Long id, @RequestParam Boolean approved) {
        log.info("Запрос на подтверждение бронирования с ID - {}", id);
        return bookingService.approveBooking(id, userId, approved);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDto approveBooking(@RequestHeader(value = "X-Sharer-User-Id", required = false)
                                     @NotNull(message = "ID пользователя должно быть указано в заголовке") Long userId,
                                     @PathVariable Long id) {
        log.info("Запрос на получение бронирования с ID - {}", id);
        return bookingService.getBookingByBookerIdOrOwnerId(id, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BookingDto> approveBooking(@RequestHeader(value = "X-Sharer-User-Id", required = false)
                                     @NotNull(message = "ID пользователя должно быть указано в заголовке") Long userId) {
        log.info("Запрос на получение бронирований пользователя с ID - {}", userId);
        return bookingService.getBookingByBooker(userId);
    }
}
