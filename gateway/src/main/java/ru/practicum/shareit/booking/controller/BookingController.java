package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.client.HttpHeader;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(@Valid @RequestBody
                                                CreateBookingRequest createRequest,
                                                @RequestHeader(HttpHeader.USER_ID)
                                                @Positive(message = "User ID must be greater than 0")
                                                long bookerId) {
        log.info("Creating booking {}, userId={}", createRequest, bookerId);
        return bookingClient.bookItem(bookerId, createRequest);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBookingStatus(@PathVariable
                                                      @Positive(message = "Booking ID must be greater than 0")
                                                      long bookingId,
                                                      @RequestParam
                                                      boolean approved,
                                                      @RequestHeader(HttpHeader.USER_ID)
                                                      @Positive(message = "User ID must be greater than 0")
                                                      long userId) {
        log.info("Updating status of booking id={} for user id={}. Status is '{}'", bookingId, userId, approved);
        return bookingClient.updateBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@PathVariable
                                                 @Positive(message = "Booking ID must be greater than 0")
                                                 long bookingId,
                                                 @RequestHeader(HttpHeader.USER_ID)
                                                 @Positive(message = "User ID must be greater than 0")
                                                 long userId) {
        log.info("Searching booking id={} for user id={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestHeader(HttpHeader.USER_ID) long userId,
              @RequestParam(name = "state", defaultValue = "all") String stateParam,
              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsByOwner(
            @RequestParam(name = "state", defaultValue = "all") String stateParam,
            @RequestHeader(HttpHeader.USER_ID) @Positive(message = "User ID must be greater than 0")  long userId,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Searching bookings for items owned by user {} with state {}", userId, stateParam);
        return bookingClient.getBookingsByOwner(userId, state, from, size);
    }

    @DeleteMapping("/{bookingId}")
    public ResponseEntity<Object> deleteBooking(@PathVariable
                                              @Positive(message = "Booking ID must be greater than 0")
                                              long bookingId,
                                              @RequestHeader(HttpHeader.USER_ID)
                                              @Positive(message = "User ID must be greater than 0")
                                              long userId) {
        log.info("Deleting booking id={} for user id={}", bookingId, userId);
        return bookingClient.deleteBooking(userId, bookingId);
    }
}
