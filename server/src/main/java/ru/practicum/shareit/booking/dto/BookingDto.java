package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.ItemBookingDto;
import ru.practicum.shareit.user.UserBookingDto;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingDto {
    Long id;
    LocalDateTime start;
    LocalDateTime end;
    ItemBookingDto item;
    UserBookingDto booker;
    BookingStatus status;
}
