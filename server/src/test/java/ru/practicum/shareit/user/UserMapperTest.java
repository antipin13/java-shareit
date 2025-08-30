package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
class UserMapperTest {
    final UserMapper userMapper = new UserMapper();

    @Test
    void toUser() {
        NewUserRequest request = new NewUserRequest();
        request.setName("Test name");
        request.setEmail("test@mail.ru");

        User user = userMapper.toUser(request);

        assertNotNull(user);
        assertEquals("Test name", user.getName());
        assertEquals("test@mail.ru", user.getEmail());
    }

    @Test
    public void toUserFromUserDto() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Test name");
        userDto.setEmail("test@mail.ru");

        User user = userMapper.toUser(userDto);

        assertEquals(1L, user.getId());
        assertEquals("Test name", user.getName());
        assertEquals("test@mail.ru", user.getEmail());
    }

    @Test
    public void toUserDto() {
        User user = new User();
        user.setId(1L);
        user.setName("Test name");
        user.setEmail("test@mail.ru");

        UserDto userDto = userMapper.toUserDto(user);

        assertEquals(1L, userDto.getId());
        assertEquals("Test name", userDto.getName());
        assertEquals("test@mail.ru", userDto.getEmail());
    }

    @Test
    public void updateUserFields() {
        User user = new User();
        user.setId(1L);
        user.setName("Test name");
        user.setEmail("test@mail.ru");

        UpdateUserRequest request = new UpdateUserRequest();
        request.setName("Update name");
        request.setEmail("updated@mail.ru");

        User updatedUser = userMapper.updateUserFields(user, request);

        assertEquals("Update name", updatedUser.getName());
        assertEquals("updated@mail.ru", updatedUser.getEmail());
    }

    @Test
    public void updateUserFieldsWithNullFields() {
        User user = new User();
        user.setId(1L);
        user.setName("Test name");
        user.setEmail("test@mail.ru");

        UpdateUserRequest request = new UpdateUserRequest();
        request.setName(null);
        request.setEmail(null);

        User result = userMapper.updateUserFields(user, request);

        assertEquals("Test name", result.getName());
        assertEquals("test@mail.ru", result.getEmail());
    }

    @Test
    public void updateUserFieldsWithBlankFields() {
        User user = new User();
        user.setId(1L);
        user.setName("Test name");
        user.setEmail("test@mail.ru");

        UpdateUserRequest request = new UpdateUserRequest();
        request.setName(" ");
        request.setEmail("   ");

        User result = userMapper.updateUserFields(user, request);

        assertEquals("Test name", result.getName());
        assertEquals("test@mail.ru", result.getEmail());
    }

    @Test
    public void toUserBookingDto() {
        User user = new User();
        user.setId(1L);

        UserBookingDto result = userMapper.toUserBookingDto(user);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

}