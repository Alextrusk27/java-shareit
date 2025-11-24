package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.sharing.HttpHeader;

import java.util.List;

@RequestMapping("/bookings")
@Validated
public interface BookingController {

    @PostMapping
    ResponseEntity<BookingDto> createBooking(@Valid @RequestBody
                                             CreateBookingRequest createRequest,
                                             @RequestHeader(HttpHeader.USER_ID)
                                             @Positive(message = "User ID must be greater than 0")
                                             Long bookerId);

    @PatchMapping("/{bookingId}")
    ResponseEntity<BookingDto> updateBookingStatus(@PathVariable
                                                   @Positive(message = "Booking ID must be greater than 0")
                                                   Long bookingId,
                                                   @RequestParam
                                                   boolean approved,
                                                   @RequestHeader(HttpHeader.USER_ID)
                                                   @Positive(message = "User ID must be greater than 0")
                                                   Long userId);

    @GetMapping("/{bookingId}")
    ResponseEntity<BookingDto> getBookingById(@PathVariable
                                              @Positive(message = "Booking ID must be greater than 0")
                                              Long bookingId,
                                              @RequestHeader(HttpHeader.USER_ID)
                                              @Positive(message = "User ID must be greater than 0")
                                              Long userId);

    @GetMapping
    ResponseEntity<List<BookingDto>> getBookingsByBookerId(@RequestParam(defaultValue = BookingState.DEFAULT)
                                                           BookingState state,
                                                           @RequestHeader(HttpHeader.USER_ID)
                                                           @Positive(message = "User ID must be greater than 0")
                                                           Long userId);

    @GetMapping("/owner")
    ResponseEntity<List<BookingDto>> getBookingsByOwnerId(@RequestParam(defaultValue = BookingState.DEFAULT)
                                                          BookingState state,
                                                          @RequestHeader(HttpHeader.USER_ID)
                                                          @Positive(message = "User ID must be greater than 0")
                                                          Long userId);

    @DeleteMapping("/{bookingId}")
    ResponseEntity<Void> deleteBookingById(@PathVariable
                                           @Positive(message = "Booking ID must be greater than 0")
                                           Long bookingId,
                                           @RequestHeader(HttpHeader.USER_ID)
                                           @Positive(message = "User ID must be greater than 0")
                                           Long userId);
}
