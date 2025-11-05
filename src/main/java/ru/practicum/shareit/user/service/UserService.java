package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public interface UserService {

    UserDto create(User user);

    UserDto update(User user, long userId);

    void delete(long userId);

    UserDto findById(long userId);
}