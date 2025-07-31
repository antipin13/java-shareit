package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewUserRequest {
    @NotBlank(message = "Поле name должно быть заполнено")
    String name;
    @NotBlank(message = "Поле email должно быть заполнено")
    @Email(message = "Неверный формат email")
    String email;
}
