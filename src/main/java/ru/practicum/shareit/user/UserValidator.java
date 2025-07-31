package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.exceptions.ConflictException;

import java.util.HashSet;
import java.util.Set;

@Slf4j
public class UserValidator {
    private static Set<String> emailsInMemory = new HashSet<>();

    public static void validateUser(User user) {
        if (isEmailTaken(user.getEmail())) {
            log.warn("Ошибка валидации: Email уже занят");
            throw new ConflictException(String.format("Email - %s уже занят", user.getEmail()));
        }

        emailsInMemory.add(user.getEmail());
    }

    private static boolean isEmailTaken(String email) {
        return emailsInMemory.contains(email);
    }
}
