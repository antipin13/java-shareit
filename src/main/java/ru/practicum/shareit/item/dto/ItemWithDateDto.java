package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Data
@Builder
public class ItemWithDateDto {
    Long id;
    String name;
    String description;
    Boolean available;
    User owner;
    ItemRequest request;
    LocalDateTime lastBookingDate;
    LocalDateTime nextBookingDate;
}