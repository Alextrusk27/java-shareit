package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record CreateBookingRequest(
        @NotNull(message = "Item ID is required")
        @Positive(message = "Item ID must be greater than 0")
        Long itemId,

        @NotNull(message = "Start date is required")
        @FutureOrPresent(message = "Start date cannot be in past")
        LocalDateTime start,

        @NotNull(message = "End date is required")
        @Future(message = "End date must be after start date")
        LocalDateTime end) {
}
