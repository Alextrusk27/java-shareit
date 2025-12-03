package ru.practicum.shareit.item.dto.projection;

import ru.practicum.shareit.booking.dto.BookingInfo;

import java.time.LocalDateTime;

public interface ItemWithBookingProjection {
    Long getId();

    String getName();

    String getDescription();

    Boolean getAvailable();

    Long getLastBookingId();

    LocalDateTime getLastBookingStart();

    LocalDateTime getLastBookingEnd();

    Long getLastBookerId();

    Long getNextBookingId();

    LocalDateTime getNextBookingStart();

    LocalDateTime getNextBookingEnd();

    Long getNextBookerId();

    default BookingInfo getLastBookingInfo() {
        return getLastBookingId() != null ?
                new BookingInfo(getLastBookingId(), getLastBookingStart(), getLastBookingEnd(), getLastBookerId())
                : null;
    }

    default BookingInfo getNextBookingInfo() {
        return getNextBookingId() != null ?
                new BookingInfo(getNextBookingId(), getNextBookingStart(), getNextBookingEnd(), getNextBookerId())
                : null;
    }
}
