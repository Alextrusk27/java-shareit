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
    UserDto create(@RequestBody @Validated(OnCreate.class) User user);

    @PatchMapping("/{id}")
    UserDto update(@RequestBody @Validated(OnPatch.class) User user,
                   @PathVariable long id);

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable long id);

    @GetMapping("/{id}")
    UserDto findById(@PathVariable long id);
}