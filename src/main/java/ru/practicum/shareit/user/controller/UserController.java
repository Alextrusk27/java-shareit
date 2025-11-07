package ru.practicum.shareit.user.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.*;

@RequestMapping("/users")
@Validated
public interface UserController {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    UserDto createUser(@RequestBody @Valid
                       CreateUserRequest createRequest);

    @PatchMapping("/{userId}")
    UserDto updateUser(@RequestBody @Valid
                       UpdateUserRequest updateRequest,
                       @PathVariable @Positive(message = "User ID must be greater than 0")
                       long userId);

    @GetMapping("/{userId}")
    UserDto getUserById(@PathVariable @Positive(message = "User ID must be greater than 0")
                        long userId);

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable long userId);
}
