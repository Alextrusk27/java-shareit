package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExtendedDto;
import ru.practicum.shareit.item.dto.request.CreateComment;
import ru.practicum.shareit.item.dto.request.CreateItem;
import ru.practicum.shareit.item.dto.request.UpdateItem;

import java.util.List;

public interface ItemService {

    ItemDto create(CreateItem createRequest, Long ownerId);

    ItemDto update(UpdateItem updateRequest, Long itemId, Long ownerId);

    ItemExtendedDto findById(Long itemId, Long userId);

    List<ItemExtendedDto> findByOwnerId(Long ownerId);

    List<ItemDto> findByQuery(String text);

    void delete(Long itemId, Long ownerId);

    CommentDto createComment(CreateComment createRequest, Long itemId, Long authorId);
}
