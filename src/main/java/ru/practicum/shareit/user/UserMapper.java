package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {
    public static User toUser(NewUserRequest request) {
        return User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .build();
    }

    public static UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static User updateUserFields(User user, UpdateUserRequest request) {
        if (request.hasId()) {
            user.setId(request.getId());
        }
        if (request.hasName()) {
            user.setName(request.getName());
        }
        if (request.hasEmail()) {
            user.setEmail(request.getEmail());
        }

        return user;
    }
}
