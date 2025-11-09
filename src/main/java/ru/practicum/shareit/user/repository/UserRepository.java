package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

public interface UserRepository {
    User save(User user);

    User update(User user);

    void delete(long id);

    User findById(long id);
}
