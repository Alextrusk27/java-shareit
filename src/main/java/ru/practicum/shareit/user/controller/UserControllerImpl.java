package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@RestController
@RequiredArgsConstructor
public class UserControllerImpl implements UserController {
    private final UserServiceImpl userService;

    @Override
    public UserDto create(User user) {
        return userService.create(user);
    }

    @Override
    public UserDto update(User user, long id) {
        return userService.update(user, id);
    }

    @Override
    public void delete(long id) {
        userService.delete(id);
    }

    @Override
    public UserDto findById(long id) {
        return userService.findById(id);
    }
}
