package ru.practicum.shareit.request.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.client.HttpHeader;
import ru.practicum.shareit.request.RequestClient;
import ru.practicum.shareit.request.dto.CreateItemRequest;

@RestController
@RequestMapping(path = "/requests")
@Slf4j
@Validated
@RequiredArgsConstructor
public class ItemRequestController {
    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader(HttpHeader.USER_ID)
                                                    @Positive(message = "User ID must be greater than 0")
                                                    Long userId,
                                                    @RequestBody @Valid
                                                    CreateItemRequest itemRequest) {
        log.info("Creating new item request '{}' from user id={}", itemRequest.description(), userId);
        return requestClient.create(userId, itemRequest);
    }

    @GetMapping
    public ResponseEntity<Object> getItemRequestByAuthor(@RequestHeader(HttpHeader.USER_ID)
                                                         @Positive(message = "User ID must be greater than 0")
                                                         Long userId) {
        log.info("Searching for item requests by user id={}", userId);
        return requestClient.getByUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequests() {
        log.info("Searching for all item requests");
        return requestClient.getAll();
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequest(@PathVariable
                                                 @Positive(message = "Request ID must be greater than 0")
                                                 Long requestId) {
        log.info("Searching for item request with id={}", requestId);
        return requestClient.getById(requestId);
    }
}
