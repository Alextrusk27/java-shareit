package ru.practicum.shareit.user.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody @Valid CreateUserRequest createRequest) {
        log.info("Creating new user");
        return userClient.createUser(createRequest);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@RequestBody @Valid UpdateUserRequest updateRequest,
                                      @PathVariable
                                      @Positive(message = "User ID must be greater than 0") long userId) {
        log.info("Updating user id={}", userId);
        return userClient.updateUser(userId, updateRequest);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable
                                       @Positive(message = "User ID must be greater than 0") long userId) {
        log.info("Searching user id={}", userId);
        return userClient.getUser(userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> delete(@PathVariable long userId) {
        log.info("Deleting user id={}", userId);
        return userClient.deleteUser(userId);
    }
}
