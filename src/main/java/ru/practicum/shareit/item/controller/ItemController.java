package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExtendedDto;
import ru.practicum.shareit.item.dto.request.CreateCommentRequest;
import ru.practicum.shareit.item.dto.request.CreateItemRequest;
import ru.practicum.shareit.item.dto.request.UpdateItemRequest;
import ru.practicum.shareit.sharing.HttpHeader;

import java.util.List;

@RequestMapping("/items")
@Validated
public interface ItemController {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ItemDto createItem(@RequestBody @Valid
                       CreateItemRequest createRequest,
                       @RequestHeader(HttpHeader.USER_ID)
                       @Positive(message = "User ID must be greater than 0")
                       long userId);

    @PatchMapping("/{id}")
    ItemDto updateItem(@RequestBody @Valid
                       UpdateItemRequest updateRequest,
                       @PathVariable @Positive(message = "Item ID must be greater than 0")
                       long id,
                       @RequestHeader(HttpHeader.USER_ID)
                       @Positive(message = "User ID must be greater than 0")
                       long userId);


    @GetMapping("/{id}")
    ItemExtendedDto getItemById(@PathVariable @Positive(message = "Item ID must be greater than 0")
                                long id);

    @GetMapping
    List<ItemExtendedDto> getOwnItems(@RequestHeader(HttpHeader.USER_ID)
                                      @Positive(message = "User ID must be greater than 0")
                                      long userId);

    @GetMapping("/search")
    List<ItemDto> searchItems(@RequestParam
                              @Pattern(regexp = "^[\\p{L}\\p{N}\\s\\-.,!?()':;%&@/]*$",
                                      message = "Query contains invalid characters")
                              @Size(max = 100, message = "Query must be no longer than 100 characters")
                              String text);

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteItem(@PathVariable @Positive(message = "Item ID must be greater than 0")
                    long id,
                    @RequestHeader(HttpHeader.USER_ID)
                    @Positive(message = "User ID must be greater than 0")
                    long userId);

    @PostMapping("/{itemId}/comment")
    CommentDto createComment(@RequestBody @Valid
                             CreateCommentRequest commentRequest,
                             @PathVariable @Positive(message = "Item ID must be greater than 0")
                             long itemId,
                             @RequestHeader(HttpHeader.USER_ID)
                             @Positive(message = "User ID must be greater than 0")
                             long authorId);
}
