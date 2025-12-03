package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

public interface BookingService {

    BookingDto createBooking(CreateBookingRequest createRequest, Long userId);

    BookingDto updateBookingStatus(Long bookingId, boolean approved, Long userId);

    BookingDto getBookingById(Long bookingId, Long userId);

    List<BookingDto> getBookingsByBookerId(Long userId, BookingState state);

    List<BookingDto> getBookingsByOwnerId(Long userId, BookingState state);

    void deleteBookingById(Long bookingId, Long userId);
}
