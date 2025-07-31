package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.request.ItemRequest;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewItemRequest {
    String name;
    String description;
    Boolean available;
    Long owner;
    ItemRequest itemRequest;
}
