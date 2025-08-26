package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import ru.practicum.shareit.exceptions.NotFoundException;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Spy
    @InjectMocks
    private UserServiceImpl userService;

    private NewUserRequest request;
    private UpdateUserRequest updatedUserRequest;
    private User user;
    private UserDto userDto;

    @Test
    void shouldSaveUserAndReturnDto() {
        request = new NewUserRequest();
        request.setName("name");
        request.setEmail("test@mail.ru");

        user = new User();
        user.setId(1L);
        user.setName("name");
        user.setEmail("test@mail.ru");

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("name");
        userDto.setEmail("test@mail.ru");

        when(userMapper.toUser(request)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toUserDto(user)).thenReturn(userDto);

        UserDto result = userService.saveUser(request);

        assertNotNull(result);
        assertEquals(userDto, result);
        verify(userMapper).toUser(request);
        verify(userRepository).save(user);
        verify(userMapper).toUserDto(user);
    }

    @Test
    void updateUser() {
        Long userId = 1L;

        updatedUserRequest = new UpdateUserRequest();
        updatedUserRequest.setName("Update name");
        updatedUserRequest.setEmail("update@mail.ru");

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setName("name");
        existingUser.setEmail("test@mail.ru");

        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setName("Update name");
        updatedUser.setEmail("update@mail.ru");

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Update name");
        userDto.setEmail("update@mail.ru");


        doReturn(existingUser).when(userService).getUserOrThrow(userId);

        when(userMapper.updateUserFields(existingUser, updatedUserRequest)).thenReturn(updatedUser);

        when(userRepository.save(updatedUser)).thenReturn(updatedUser);

        when(userMapper.toUserDto(updatedUser)).thenReturn(userDto);

        UserDto result = userService.updateUser(updatedUserRequest, userId);

        assertNotNull(result);
        assertEquals(userDto, result);

        verify(userMapper).updateUserFields(existingUser, updatedUserRequest);
        verify(userRepository).save(updatedUser);
        verify(userMapper).toUserDto(updatedUser);
    }

    @Test
    void findUserById() {
        Long userId = 1L;

        User user = new User();
        user.setId(userId);
        user.setName("Test User");
        user.setEmail("test@mail.ru");

        userDto = new UserDto();
        userDto.setId(userId);
        userDto.setName("Test User");
        userDto.setEmail("test@mail.ru");

        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));

        when(userMapper.toUserDto(user)).thenReturn(userDto);

        UserDto result = userService.findUserById(userId);

        assertNotNull(result);
        assertEquals(userDto, result);

        verify(userRepository).findById(userId);
        verify(userMapper).toUserDto(user);
    }

    @Test
    void findUserByIdNotFound() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(java.util.Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.findUserById(userId));

        verify(userRepository).findById(userId);
    }

    @Test
    void removeUser() {
        Long userId = 1L;

        User user = new User();
        user.setId(userId);
        doReturn(user).when(userService).getUserOrThrow(userId);

        userService.removeUser(userId);

        verify(userRepository).delete(user);
    }

}