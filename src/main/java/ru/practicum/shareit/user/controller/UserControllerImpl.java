package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserControllerImpl implements UserController {
    private final UserServiceImpl userService;

    @Override
    public UserDto create(User user) {
        log.info("POST request received: new user create");
        return userService.create(user);
    }

    @Override
    public UserDto update(User user, long id) {
        log.info("PATCH request received: user ID:{} update", id);
        return userService.update(user, id);
    }

    @Override
    public void delete(long id) {
        log.info("DELETE request received: user ID:{} delete", id);
        userService.delete(id);
    }

    @Override
    public UserDto findById(long id) {
        log.info("GET request received: get user ID:{}", id);
        return userService.findById(id);
    }
}
