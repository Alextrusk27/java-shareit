package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateItem(
        @Pattern(regexp = "^[\\p{L}\\p{N}\\s\\-.,!?()']*$", message = "Item title contains invalid characters")
        @Size(max = 50, message = "Item title must be no longer than 50 characters")
        String name,

        @Pattern(regexp = "^[\\p{L}\\p{N}\\s\\-.,!?()':;%&@/]*$", message = "Description contains invalid characters")
        @Size(max = 300, message = "Description must be no longer than 300 characters")
        String description,

        Boolean available
) {
}