package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.Optional;

public interface UserRepository {
    void save(User user);

    void update(User user, long id);

    void delete(long id);

    Optional<User> findById(long id);
}
