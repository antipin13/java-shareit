package ru.practicum.shareit.item;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewCommentRequest {
    @NotBlank(message = "Поле text должно быть заполнено")
    String text;
}
