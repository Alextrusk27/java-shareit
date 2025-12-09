package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

public record CreateBookingRequest(long itemId, LocalDateTime start, LocalDateTime end) {
}
