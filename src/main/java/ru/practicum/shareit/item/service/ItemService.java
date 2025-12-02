package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExtendedDto;
import ru.practicum.shareit.item.dto.request.CreateCommentRequest;
import ru.practicum.shareit.item.dto.request.CreateItemRequest;
import ru.practicum.shareit.item.dto.request.UpdateItemRequest;

import java.util.List;

public interface ItemService {

    ItemDto create(CreateItemRequest createRequest, Long ownerId);

    ItemDto update(UpdateItemRequest updateRequest, Long itemId, Long ownerId);

    ItemExtendedDto findById(Long itemId, Long userId);

    List<ItemExtendedDto> findByOwnerId(Long ownerId);

    List<ItemDto> findByQuery(String text);

    void delete(Long itemId, Long ownerId);

    CommentDto createComment(CreateCommentRequest createRequest, Long itemId, Long authorId);
}
