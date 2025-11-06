package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.*;

public interface UserService {

    UserDto create(CreateUserRequest createRequest);

    UserDto update(UpdateUserRequest updateRequest, long userId);

    void delete(long userId);

    UserDto findById(long userId);
}