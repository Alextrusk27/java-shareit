package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.*;
import ru.practicum.shareit.user.service.UserServiceImpl;

@RequestMapping("/users")
@RestController
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserServiceImpl userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    UserDto createUser(@RequestBody CreateUserRequest createRequest) {
        log.info("Creating new user");
        UserDto result = userService.create(createRequest);
        log.info("User created: id={}", result.id());
        return result;
    }

    @PatchMapping("/{userId}")
    UserDto updateUser(@RequestBody UpdateUserRequest updateRequest,
                       @PathVariable long userId) {
        log.info("Updating user id={}", userId);
        UserDto result = userService.update(updateRequest, userId);
        log.info("User updated: id={}", result.id());
        return result;
    }

    @GetMapping("/{userId}")
    UserDto getUserById(@PathVariable long userId) {
        log.info("Searching user id={}", userId);
        UserDto result = userService.findById(userId);
        log.info("User id={} was found", result.id());
        return result;
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable long userId) {
        log.info("Deleting user id={}", userId);
        userService.delete(userId);
        log.info("User id={} was deleted", userId);
    }
}
