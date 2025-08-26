package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserDtoTest {
    @Test
    public void userDtoBuilder() {
        UserDto user = new UserDto();
        user.setId(1L);
        user.setName("Test name");
        user.setEmail("test@mail.ru");

        assertNotNull(user);
        assertEquals(1L, user.getId());
        assertEquals("Test name", user.getName());
        assertEquals("test@mail.ru", user.getEmail());
    }

}