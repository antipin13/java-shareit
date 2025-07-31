package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewItemRequest {
    @NotBlank(message = "Поле name должно быть заполнено")
    String name;
    @NotBlank(message = "Поле description должно быть заполнено")
    String description;
    @NotNull(message = "Поле available должно быть заполнено")
    Boolean available;
}
