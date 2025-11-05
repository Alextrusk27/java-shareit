package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserControllerImpl implements UserController {
    private final UserServiceImpl userService;

    @Override
    public UserDto createUser(User user) {
        log.info("Creating new user");
        UserDto result = userService.create(user);
        log.info("User created: id={}", result.id());
        return result;
    }

    @Override
    public UserDto updateUser(User user, long userId) {
        log.info("Updating user id={}", userId);
        UserDto result = userService.update(user, userId);
        log.info("User updated: id={}", result.id());
        return result;
    }

    @Override
    public void deleteUser(long userId) {
        log.info("Deleting user id={}", userId);
        userService.delete(userId);
    }

    @Override
    public UserDto getUserById(long userId) {
        log.info("Searching user id={}", userId);
        UserDto result = userService.findById(userId);
        log.info("User id={} was found", result.id());
        return result;
    }
}
