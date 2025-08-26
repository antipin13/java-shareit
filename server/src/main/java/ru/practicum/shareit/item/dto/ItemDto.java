package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

@Data
public class ItemDto {
    Long id;
    String name;
    String description;
    Boolean available;
    User owner;
    ItemRequest request;
}
