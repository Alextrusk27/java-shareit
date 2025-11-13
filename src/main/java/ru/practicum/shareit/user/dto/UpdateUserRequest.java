package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
        @Pattern(regexp = "^[\\p{L}\\p{N}\\s\\-.,!?()']*$", message = "User name contains invalid characters")
        @Size(max = 30, message = "Name must be no longer than 30 characters")
        String name,

        @Email(message = "Invalid email format")
        String email
) {
    public boolean hasName() {
        return name != null && !name.isBlank();
    }

    public boolean hasEmail() {
        return email != null;
    }
}
