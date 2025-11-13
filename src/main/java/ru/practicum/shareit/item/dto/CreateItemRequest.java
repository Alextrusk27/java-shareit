package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateItemRequest(
        @NotBlank(message = "Item name cannot be empty")
        @Pattern(regexp = "^[\\p{L}\\d\\s]+$", message = "Item name contains invalid characters")
        @Size(max = 50, message = "Name must be no longer than 50 characters")
        String name,

        @NotBlank(message = "Item description cannot be empty")
        @Pattern(regexp = "^[\\p{L}\\d\\s]+$", message = "Item description contains invalid characters")
        @Size(max = 300, message = "Description must be no longer than 300 characters")
        String description,

        @NotNull(message = "Available status is required")
        Boolean available
) {
}
