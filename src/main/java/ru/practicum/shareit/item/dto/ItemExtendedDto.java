package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.dto.BookingInfo;

import java.util.List;

public record ItemExtendedDto(Long id,
                              String name,
                              String description,
                              Boolean available,
                              BookingInfo lastBooking,
                              BookingInfo nextBooking,
                              List<CommentDto> comments) {
    public ItemExtendedDto {
        lastBooking = lastBooking != null && lastBooking.id() == null ? null : lastBooking;
        nextBooking = nextBooking != null && nextBooking.id() == null ? null : nextBooking;
        comments = comments != null ? comments : List.of();  // Защита от null
    }
}
