package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.ValidationException;

import java.util.HashSet;
import java.util.Set;

@Slf4j
public class UserValidator {
    private static Set<String> emailsInMemory = new HashSet<>();

    public static void validateUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.warn("Ошибка валидации: Email не может быть пустым или не содержать символ - @");
            throw new ValidationException(user.getEmail(), "Email не может быть пустым или не содержать символ - @");
        }

        if (isEmailTaken(user.getEmail())) {
            log.warn("Ошибка валидации: Email уже занят");
            throw new ConflictException(user.getEmail(), "Email уже занят");
        }

        emailsInMemory.add(user.getEmail());
    }

    private static boolean isEmailTaken(String email) {
        return emailsInMemory.contains(email);
    }
}
