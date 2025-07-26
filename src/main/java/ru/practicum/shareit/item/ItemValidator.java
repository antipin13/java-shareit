package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;

@Slf4j
public class ItemValidator {
    public static void validateItem(Item item) {
        if (item.getName() == null || item.getName().isBlank()) {
            log.warn("Ошибка валидации: Поле name не может быть пустым");
            throw new ValidationException(item.getName(), "Поле name не может быть пустым");
        }

        if (item.getDescription() == null || item.getDescription().isBlank()) {
            log.warn("Ошибка валидации: Поле description не может быть пустым");
            throw new ValidationException(item.getName(), "Поле description не может быть пустым");
        }

        if (item.getAvailable() == null) {
            log.warn("Ошибка валидации: Поле available не может быть пустым");
            throw new ValidationException(item.getName(), "Поле available не может быть пустым");
        }
    }
}
