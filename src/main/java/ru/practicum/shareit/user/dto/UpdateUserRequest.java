package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateUserRequest {
    private String name;
    @Email(message = "Неверный формат email")
    private String email;
}
