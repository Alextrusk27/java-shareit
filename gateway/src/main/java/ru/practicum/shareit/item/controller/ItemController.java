package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.client.HttpHeader;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.dto.CreateComment;
import ru.practicum.shareit.item.dto.CreateItem;
import ru.practicum.shareit.item.dto.UpdateItem;

@RequestMapping("/items")
@Validated
@Slf4j
@RestController
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestBody @Valid
                                      CreateItem createRequest,
                                      @RequestHeader(HttpHeader.USER_ID)
                                      @Positive(message = "User ID must be greater than 0")
                                      long userId) {
        log.info("Creating new item {} for user id={}{}", createRequest.name(), userId,
                createRequest.requestId() != null ? " on request id=%d".formatted(createRequest.requestId()) : "");
        return itemClient.create(userId, createRequest);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateItem(@RequestBody @Valid
                                      UpdateItem updateRequest,
                                      @PathVariable @Positive(message = "Item ID must be greater than 0")
                                      long id,
                                      @RequestHeader(HttpHeader.USER_ID)
                                      @Positive(message = "User ID must be greater than 0")
                                      long userId) {
        log.info("Updating item id={} for user id={}", id, userId);
        return itemClient.update(userId, id, updateRequest);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Object> getItemById(@PathVariable @Positive(message = "Item ID must be greater than 0")
                                       long id,
                                       @RequestHeader(HttpHeader.USER_ID)
                                       @Positive(message = "User ID must be greater than 0")
                                       long userId) {
        log.info("Searching item id={}", id);
        return itemClient.getById(userId, id);
    }

    @GetMapping
    public ResponseEntity<Object> getOwnItems(@RequestHeader(HttpHeader.USER_ID)
                                       @Positive(message = "User ID must be greater than 0")
                                       long userId) {
        log.info("Searching all own items from user id={}", userId);
        return itemClient.getAll(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam
                                       @Pattern(regexp = "^[\\p{L}\\p{N}\\s\\-.,!?()':;%&@/]*$",
                                               message = "Query contains invalid characters")
                                       @Size(max = 100, message = "Query must be no longer than 100 characters")
                                       String text,
                                       @RequestHeader(HttpHeader.USER_ID)
                                       @Positive(message = "User ID must be greater than 0")
                                       long userId) {
        log.info("Searching available items by query: {}", text);
        return itemClient.getByQuery(userId, text);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteItem(@PathVariable @Positive(message = "Item ID must be greater than 0")
                                      long id,
                                      @RequestHeader(HttpHeader.USER_ID)
                                      @Positive(message = "User ID must be greater than 0")
                                      long userId) {
        log.info("Deleting item id={} by user id={}", id, userId);
        return itemClient.delete(userId, id);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestBody @Valid
                                         CreateComment commentRequest,
                                         @PathVariable @Positive(message = "Item ID must be greater than 0")
                                         long itemId,
                                         @RequestHeader(HttpHeader.USER_ID)
                                         @Positive(message = "User ID must be greater than 0")
                                         long userId) {
        log.info("Creating new comment {} by author id={} for item id={}", commentRequest.text(), userId, itemId);
        return itemClient.createComment(userId, itemId, commentRequest);
    }
}
