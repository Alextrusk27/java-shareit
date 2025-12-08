package ru.practicum.shareit.item.dto.request;

import jakarta.validation.constraints.*;

public record CreateItem(
        @NotBlank(message = "Item name cannot be empty")
        @Pattern(regexp = "^[\\p{L}\\d\\s]+$", message = "Item name contains invalid characters")
        @Size(max = 50, message = "Name must be no longer than 50 characters")
        String name,

        @NotBlank(message = "Item description cannot be empty")
        @Pattern(regexp = "^[\\p{L}\\d\\s]+$", message = "Item description contains invalid characters")
        @Size(max = 300, message = "Description must be no longer than 300 characters")
        String description,

        @NotNull(message = "Available status is required")
        Boolean available,

        @Positive(message = "Request ID must be greater than 0")
        Long requestId
) {
}
