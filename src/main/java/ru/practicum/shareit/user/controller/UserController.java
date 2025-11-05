package ru.practicum.shareit.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.validation.groups.OnCreate;
import ru.practicum.shareit.common.validation.groups.OnPatch;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@RequestMapping(path = "/users")
public interface UserController {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    UserDto createUser(@RequestBody @Validated(OnCreate.class) User user);

    @PatchMapping("/{userId}")
    UserDto updateUser(@RequestBody @Validated(OnPatch.class) User user,
                   @PathVariable long userId);

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteUser(@PathVariable long userId);

    @GetMapping("/{userId}")
    UserDto getUserById(@PathVariable long userId);
}