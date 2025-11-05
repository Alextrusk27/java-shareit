package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import java.util.List;

public interface ItemService {

    ItemDto create(CreateItemRequest createRequest, long ownerId);

    ItemDto update(UpdateItemRequest updateRequest, long itemId, long ownerId);

    ItemDto findById(long itemId);

    List<ItemDto> findByOwnerId(long ownerId);

    List<ItemDto> findByQuery(String text);
}
