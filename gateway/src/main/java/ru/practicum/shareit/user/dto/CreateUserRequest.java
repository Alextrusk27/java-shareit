package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @NotBlank(message = "User name cannot be empty")
        @Pattern(regexp = "^[\\p{L}\\p{N}\\s\\-.,!?()']*$", message = "User name contains invalid characters")
        @Size(max = 30, message = "Name must be no longer than 30 characters")
        String name,

        @NotBlank(message = "Email cannot be empty")
        @Email(message = "Invalid email format")
        String email
) {
}