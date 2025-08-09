package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    final UserRepository userRepository;
    final UserMapper userMapper;
    static final String NOT_FOUND_MESSAGE = "Пользователь с ID - %d не найден";

    @Override
    public UserDto saveUser(NewUserRequest request) {
        User user = userMapper.toUser(request);

        user = userRepository.save(user);

        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(UpdateUserRequest request, Long userId) {
        User existingUser = getUserOrThrow(userId);

        existingUser = userMapper.updateUserFields(existingUser, request);

        existingUser = userRepository.save(existingUser);

        return userMapper.toUserDto(existingUser);
    }

    @Override
    public UserDto findUserById(Long userId) {
        return userMapper.toUserDto(getUserOrThrow(userId));
    }

    @Override
    public void removeUser(Long userId) {
        User user = getUserOrThrow(userId);

        userRepository.delete(user);
    }

    public User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_MESSAGE, userId)));
    }
}
