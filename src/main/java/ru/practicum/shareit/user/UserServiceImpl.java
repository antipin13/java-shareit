package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserServiceImpl implements UserService {
    final UserRepository userRepository;
    static final String NOT_FOUND_MESSAGE = "Пользователь с ID - %d не найден";

    public UserServiceImpl(@Qualifier("InMemoryStorage") UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto saveUser(NewUserRequest request) {
        User user = UserMapper.toUser(request);

        UserValidator.validateUser(user);

        user = userRepository.save(user);

        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(UpdateUserRequest request, Long userId) {
        User existingUser = userRepository.findUserById(userId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_MESSAGE, userId)));

        String oldEmail = existingUser.getEmail();

        existingUser = UserMapper.updateUserFields(existingUser, request);

        if (!existingUser.getEmail().equals(oldEmail)) {
            UserValidator.validateUser(existingUser);
        }

        existingUser = userRepository.update(existingUser);

        return UserMapper.toUserDto(existingUser);
    }

    @Override
    public UserDto findUserById(Long userId) {
        return userRepository.findUserById(userId)
                .map(UserMapper::toUserDto)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_MESSAGE, userId)));
    }

    @Override
    public void removeUser(Long userId) {
        userRepository.findUserById(userId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_MESSAGE, userId)));

        userRepository.remove(userId);
    }
}
