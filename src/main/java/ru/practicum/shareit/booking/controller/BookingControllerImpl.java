package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class BookingControllerImpl implements BookingController {
    private final BookingService bookingService;

    @Override
    public ResponseEntity<BookingDto> createBooking(CreateBookingRequest createBookingRequest, Long bookerId) {
        log.info("Creating a new booking: item id={} for user id={}", createBookingRequest.itemId(), bookerId);
        BookingDto result = bookingService.createBooking(createBookingRequest, bookerId);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(result.id())
                .toUri();
        log.info("Booking id={} created with status={}", result.id(), result.status());
        return ResponseEntity.created(location).body(result);
    }

    @Override
    public ResponseEntity<BookingDto> updateBookingStatus(Long bookingId, boolean approved, Long userId) {
        log.info("Updating status of booking id={} for user id={}. Status is '{}'", bookingId, userId, approved);
        BookingDto result = bookingService.updateBookingStatus(bookingId, approved, userId);
        log.info("Booking id={} updated with status={}", bookingId, result.status());
        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<BookingDto> getBookingById(Long bookingId, Long userId) {
        log.info("Searching booking id={} for user id={}", bookingId, userId);
        BookingDto result = bookingService.getBookingById(bookingId, userId);
        log.info("Booking id={} was found", bookingId);
        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<List<BookingDto>> getBookingsByBookerId(BookingState state, Long userId) {
        log.info("Searching all own bookings with state={} by user id={}", state, userId);
        List<BookingDto> result = bookingService.getBookingsByBookerId(userId, state);
        log.info("Found {} bookings for user {} with state {}", result.size(), userId, state);
        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<List<BookingDto>> getBookingsByOwnerId(BookingState state, Long userId) {
        log.info("Searching bookings for items owned by user {} with state {}", userId, state);
        List<BookingDto> result = bookingService.getBookingsByOwnerId(userId, state);
        log.info("Found {} bookings for items owned by user {}", result.size(), userId);
        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<Void> deleteBookingById(Long bookingId, Long userId) {
        log.info("Deleting booking id={} for user id={}", bookingId, userId);
        bookingService.deleteBookingById(bookingId, userId);
        log.info("Booking id={} successfully deleted by user id={}", bookingId, userId);
        return ResponseEntity.noContent().build();
    }
}
