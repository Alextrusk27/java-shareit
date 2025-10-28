package ru.practicum.shareit.user.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.common.validation.groups.OnCreate;
import ru.practicum.shareit.common.validation.groups.OnPatch;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    Long id;

    @NotBlank(message = "Name cannot be empty", groups = OnCreate.class)
    String name;

    @NotBlank(message = "Email cannot be empty", groups = OnCreate.class)
    @Email(message = "Invalid email format", groups = {OnCreate.class, OnPatch.class})
    String email;
}