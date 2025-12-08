package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExtendedDto;
import ru.practicum.shareit.item.dto.request.CreateComment;
import ru.practicum.shareit.item.dto.request.CreateItem;
import ru.practicum.shareit.item.dto.request.UpdateItem;
import ru.practicum.shareit.item.service.ItemServiceImpl;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ItemControllerImpl implements ItemController {
    private final ItemServiceImpl itemService;

    @Override
    public ItemDto createItem(CreateItem createRequest, long userId) {
        log.info("Creating new item {} for user id={}{}", createRequest.name(), userId,
                createRequest.requestId() != null ? " on request id=%d".formatted(createRequest.requestId()) : "");
        ItemDto result = itemService.create(createRequest, userId);
        log.info("Item created: id={} for user id={}", result.id(), userId);
        return result;
    }

    @Override
    public ItemDto updateItem(UpdateItem updateRequest, long id, long userId) {
        log.info("Updating item id={} for user id={}", id, userId);
        ItemDto result = itemService.update(updateRequest, id, userId);
        log.info("Item updated: id={} for user id={}", id, userId);
        return result;
    }

    @Override
    public ItemExtendedDto getItemById(long id, long userId) {
        log.info("Searching item id={}", id);
        ItemExtendedDto result = itemService.findById(id, userId);
        log.info("Item id={} was found", id);
        return result;
    }

    @Override
    public List<ItemExtendedDto> getOwnItems(long userId) {
        log.info("Searching all own items from user id={}", userId);
        List<ItemExtendedDto> result = itemService.findByOwnerId(userId);
        log.info("Search result (owm items) by user id={}: {}", userId, getItemsLog(result));
        return result;
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        log.info("Searching available items by query: {}", text);
        List<ItemDto> result = itemService.findByQuery(text);
        log.info("Search result by query: '{}': {}", text, getItemsLog(result));
        return result;
    }

    @Override
    public void deleteItem(long id, long userId) {
        log.info("Deleting item id={} by user id={}", id, userId);
        itemService.delete(id, userId);
        log.info("Item id={} was deleted by owner id={}", id, userId);
    }

    @Override
    public CommentDto createComment(CreateComment commentRequest, long itemId, long authorId) {
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
