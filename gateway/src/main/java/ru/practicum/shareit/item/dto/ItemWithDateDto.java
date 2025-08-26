package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ItemWithDateDto {
    Long id;
    String name;
    String description;
    Boolean available;
    Long ownerId;
    Long requestId;
    LocalDateTime lastBookingDate;
    LocalDateTime nextBookingDate;
}