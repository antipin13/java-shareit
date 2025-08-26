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
class UpdateUserRequestTest {
    static Validator validator;

    @BeforeAll
    public static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void validUpdateUserRequest() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setName("Test name");
        request.setEmail("test@mail.ru");

        Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Должны отсутствовать нарушения");
    }

    @Test
    public void invalidEmailFormat() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setName("Test name");
        request.setEmail("invalid-test");

        Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Неверный формат email")));
    }

    @Test
    public void nullEmail() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setName("Test name");
        request.setEmail(null);

        Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Email может быть null, проверка не запрещает");
    }
}