package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

public record BookingInfo(
        Long id,
        LocalDateTime start,
        LocalDateTime end,
        Long bookerId
) {
}
