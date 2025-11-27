package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.dto.BookingInfo;

public record ItemWithBookingsDto(Long id, String name, String description, Boolean available,
                                  BookingInfo lastBooking, BookingInfo nextBooking) {
    public ItemWithBookingsDto {
        lastBooking = lastBooking.id() == null ? null : lastBooking;
        nextBooking = nextBooking.id() == null ? null : nextBooking;
    }
}
