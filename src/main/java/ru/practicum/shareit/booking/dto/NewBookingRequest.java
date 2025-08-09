package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewBookingRequest {
    @NotNull(message = "Поле itemId должно быть заполнено")
    Long itemId;
    @NotNull(message = "Поле start должно быть заполнено")
    LocalDateTime start;
    @NotNull(message = "Поле end должно быть заполнено")
    LocalDateTime end;
}
