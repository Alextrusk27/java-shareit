package ru.practicum.shareit.item.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateComment(
        @NotBlank(message = "Comment cannot be empty")
        @Size(max = 500, message = "Comment must be no longer than 500 characters")
        String text) {
}
