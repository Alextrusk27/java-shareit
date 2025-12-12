package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.sharing.HttpHeader;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/bookings")
@Slf4j
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    ResponseEntity<BookingDto> createBooking(@RequestBody CreateBookingRequest createRequest,
                                             @RequestHeader(HttpHeader.USER_ID) long bookerId) {
        log.info("Creating a new booking: item id={} for user id={}", createRequest.itemId(), bookerId);
        BookingDto result = bookingService.createBooking(createRequest, bookerId);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(result.id())
                .toUri();
        log.info("Booking id={} created with status={}", result.id(), result.status());
        return ResponseEntity.created(location).body(result);
    }

    @PatchMapping("/{bookingId}")
    ResponseEntity<BookingDto> updateBookingStatus(@PathVariable long bookingId,
                                                   @RequestParam boolean approved,
                                                   @RequestHeader(HttpHeader.USER_ID) long userId) {
        log.info("Updating status of booking id={} for user id={}. Status is '{}'", bookingId, userId, approved);
        BookingDto result = bookingService.updateBookingStatus(bookingId, approved, userId);
        log.info("Booking id={} updated with status={}", bookingId, result.status());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{bookingId}")
    ResponseEntity<BookingDto> getBookingById(@PathVariable long bookingId,
                                              @RequestHeader(HttpHeader.USER_ID) long userId) {
        log.info("Searching booking id={} for user id={}", bookingId, userId);
        BookingDto result = bookingService.getBookingById(bookingId, userId);
        log.info("Booking id={} was found", bookingId);
        return ResponseEntity.ok(result);
    }

    @GetMapping
    ResponseEntity<List<BookingDto>> getBookingsByBookerId(@RequestParam BookingState state,
                                                           @RequestHeader(HttpHeader.USER_ID) long userId) {
        log.info("Searching all own bookings with state={} by user id={}", state, userId);
        List<BookingDto> result = bookingService.getBookingsByBookerId(userId, state);
        log.info("Found {} bookings for user {} with state {}", result.size(), userId, state);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/owner")
    ResponseEntity<List<BookingDto>> getBookingsByOwnerId(@RequestParam BookingState state,
                                                          @RequestHeader(HttpHeader.USER_ID) long userId) {
        log.info("Searching bookings for items owned by user {} with state {}", userId, state);
        List<BookingDto> result = bookingService.getBookingsByOwnerId(userId, state);
        log.info("Found {} bookings for items owned by user {}", result.size(), userId);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{bookingId}")
    ResponseEntity<Void> deleteBookingById(@PathVariable long bookingId,
                                           @RequestHeader(HttpHeader.USER_ID) long userId) {
        log.info("Deleting booking id={} for user id={}", bookingId, userId);
        bookingService.deleteBookingById(bookingId, userId);
        log.info("Booking id={} successfully deleted by user id={}", bookingId, userId);
        return ResponseEntity.noContent().build();
    }
}

