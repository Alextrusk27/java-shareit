package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public interface UserService {

    UserDto create(User user);

    UserDto update(User user, long id);

    void delete(long id);

    UserDto findById(long id);
}