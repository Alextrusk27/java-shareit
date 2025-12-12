package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateItemRequest(
        @NotBlank
        @Size(max = 500, message = "Description must be no longer than 500 characters")
        String description
) {
}
