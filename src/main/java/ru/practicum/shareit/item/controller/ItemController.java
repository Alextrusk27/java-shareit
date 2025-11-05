package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;

import java.util.List;

@Validated
@RequestMapping("/items")
public interface ItemController {
    String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ItemDto createItem(@RequestBody @Valid
                       CreateItemRequest createRequest,
                       @RequestHeader(USER_ID_HEADER)
                       @Positive(message = "User ID must be greater than 0")
                       long ownerId);

    @PatchMapping("/{itemId}")
    ItemDto updateItem(@RequestBody @Valid
                       UpdateItemRequest updateRequest,
                       @PathVariable @Positive(message = "Item ID must be greater than 0")
                       long itemId,
                       @RequestHeader(USER_ID_HEADER)
                       @Positive(message = "User ID must be greater than 0")
                       long ownerId);


    @GetMapping("/{itemId}")
    ItemDto getItemById(@PathVariable @Positive(message = "Item ID must be greater than 0")
                        long itemId);

    @GetMapping
    List<ItemDto> getOwnItems(@RequestHeader(USER_ID_HEADER)
                              @Positive(message = "User ID must be greater than 0")
                              long ownerId);

    @GetMapping("/search")
    List<ItemDto> searchItems(@RequestParam
                              @Pattern(regexp = "^[\\p{L}\\p{N}\\s\\-.,!?()':;%&@/]*$",
                                      message = "Query contains invalid characters")
                              @Size(max = 100, message = "Query must be no longer than 100 characters")
                              String text);

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteItem(@PathVariable @Positive(message = "Item ID must be greater than 0")
                    long itemId,
                    @RequestHeader(USER_ID_HEADER)
                    @Positive(message = "User ID must be greater than 0")
                    long ownerId);
}
