package ru.practicum.shareit.user.dto;

import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
class NewUserRequestTest {
    static Validator validator;

    @BeforeAll
    public static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void validRequest() {
        NewUserRequest request = new NewUserRequest();
        request.setName("Test name");
        request.setEmail("test@mail.ru");

        Set<ConstraintViolation<NewUserRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Должны отсутствовать нарушения");
    }

    @Test
    public void blankName() {
        NewUserRequest request = new NewUserRequest();
        request.setName(" ");
        request.setEmail("test@mail.ru");

        Set<ConstraintViolation<NewUserRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Поле name должно быть заполнено")));
    }

    @Test
    public void invalidEmail() {
        NewUserRequest request = new NewUserRequest();
        request.setName("test");
        request.setEmail("invalid-test");

        Set<ConstraintViolation<NewUserRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Неверный формат email")));
    }
}