package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@FieldDefaults(level = AccessLevel.PRIVATE)
@Qualifier("InMemoryStorage")
public class InMemoryUserRepository implements UserRepository {
    final List<User> users = new ArrayList<>();

    @Override
    public User save(User user) {
        user.setId(getId());
        users.add(user);
        return user;
    }

    @Override
    public User update(User user) {
        Optional<User> existingUserOpt = findUserById(user.getId());
        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();
            int index = users.indexOf(existingUser);
            users.set(index, user);
            return user;
        }
        return null;
    }

    @Override
    public Optional<User> findUserById(Long userId) {
        for (User user : users) {
            if (user.getId().equals(userId)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    @Override
    public void remove(Long id) {
        Optional<User> existingUserOpt = findUserById(id);
        if (existingUserOpt.isPresent()) {
            users.remove(existingUserOpt.get());
        }
    }

    private long getId() {
        long lastId = users.stream()
                .mapToLong(User::getId)
                .max()
                .orElse(0);
        return lastId + 1;
    }
}
