package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;



public interface UserService {
    UserDto saveUser(NewUserRequest request);

    UserDto updateUser(UpdateUserRequest request, Long userId);

    UserDto findUserById(Long id);

    void removeUser(Long id);
}
