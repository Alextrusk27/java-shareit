package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExtendedDto;
import ru.practicum.shareit.item.dto.request.CreateComment;
import ru.practicum.shareit.item.dto.request.CreateItem;
import ru.practicum.shareit.item.dto.request.UpdateItem;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.sharing.HttpHeader;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
public class ItemController {
    private final ItemServiceImpl itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ItemDto createItem(@RequestBody CreateItem createRequest,
                       @RequestHeader(HttpHeader.USER_ID) long userId) {
        log.info("Creating new item {} for user id={}{}", createRequest.name(), userId,
                createRequest.requestId() != null ? " on request id=%d".formatted(createRequest.requestId()) : "");
        ItemDto result = itemService.create(createRequest, userId);
        log.info("Item created: id={} for user id={}", result.id(), userId);
        return result;
    }

    @PatchMapping("/{id}")
    ItemDto updateItem(@RequestBody UpdateItem updateRequest,
                       @PathVariable long id,
                       @RequestHeader(HttpHeader.USER_ID) long userId) {
        log.info("Updating item id={} for user id={}", id, userId);
        ItemDto result = itemService.update(updateRequest, id, userId);
        log.info("Item updated: id={} for user id={}", id, userId);
        return result;
    }

    @GetMapping("/{id}")
    ItemExtendedDto getItemById(@PathVariable long id,
                                @RequestHeader(HttpHeader.USER_ID) long userId) {
        log.info("Searching item id={}", id);
        ItemExtendedDto result = itemService.findById(id, userId);
        log.info("Item id={} was found", id);
        return result;
    }

    @GetMapping
    List<ItemExtendedDto> getOwnItems(@RequestHeader(HttpHeader.USER_ID) long userId) {
        log.info("Searching all own items from user id={}", userId);
        List<ItemExtendedDto> result = itemService.findByOwnerId(userId);
        log.info("Search result (owm items) by user id={}: {}", userId, getItemsLog(result));
        return result;
    }

    @GetMapping("/search")
    List<ItemDto> searchItems(@RequestParam String text) {
        log.info("Searching available items by query: {}", text);
        List<ItemDto> result = itemService.findByQuery(text);
        log.info("Search result by query: '{}': {}", text, getItemsLog(result));
        return result;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteItem(@PathVariable long id,
                    @RequestHeader(HttpHeader.USER_ID) long userId) {
        log.info("Deleting item id={} by user id={}", id, userId);
        itemService.delete(id, userId);
        log.info("Item id={} was deleted by owner id={}", id, userId);
    }

    @PostMapping("/{itemId}/comment")
    CommentDto createComment(@RequestBody CreateComment commentRequest,
                             @PathVariable long itemId,
                             @RequestHeader(HttpHeader.USER_ID) long authorId) {
        log.info("Creating new comment {} by author id={} for item id={}", commentRequest.text(), authorId, itemId);
        CommentDto comment = itemService.createComment(commentRequest, itemId, authorId);
        log.info("Comment created: id={}", comment.id());
        return comment;
    }

    private <T> String getItemsLog(List<T> result) {
        if (result.isEmpty()) {
            return "No items found";
        }
        return IntStream.range(0, result.size())
                .mapToObj(i -> String.format("%d. %s", i + 1, result.get(i)))
                .collect(Collectors.joining("; "));
    }
}
