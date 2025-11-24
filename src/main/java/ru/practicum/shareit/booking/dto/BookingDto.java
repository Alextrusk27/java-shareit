package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

public record BookingDto(Long id,
                         ItemDto item,
                         UserDto booker,
                         LocalDateTime start,
                         LocalDateTime end,
                         BookingStatus status) {
}
