package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;

public interface BookingService {
    BookingDto saveBooking(Long userId, NewBookingRequest request);

    BookingDto approveBooking(Long bookingId, Long userId, Boolean approved);

    BookingDto getBookingByBookerIdOrOwnerId(Long bookingId, Long userId);
}
