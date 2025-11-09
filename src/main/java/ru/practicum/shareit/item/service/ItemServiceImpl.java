package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.InMemoryItemRepository;
import ru.practicum.shareit.user.repository.InMemoryUserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final InMemoryItemRepository itemRepository;
    private final InMemoryUserRepository userRepository;

    @Override
    public ItemDto create(CreateItemRequest createRequest, long ownerId) {
        userRepository.validateUserExists(ownerId);
        Item newItem = ItemMapper.mapCreateRequestToItem(createRequest, ownerId);
        newItem = itemRepository.save(newItem);
        return ItemMapper.mapItemToDto(newItem);
    }

    @Override
    public ItemDto update(UpdateItemRequest updateRequest, long itemId, long ownerId) {
        userRepository.validateUserExists(ownerId);
        itemRepository.validateItemOwner(itemId, ownerId);
        Item updateItem = ItemMapper.mapUpdateRequestToItem(updateRequest, itemId);
        updateItem = itemRepository.update(updateItem, itemId);
        return ItemMapper.mapItemToDto(updateItem);
    }

    @Override
    public ItemDto findById(long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Item id=%d not found".formatted(itemId)));
        return ItemMapper.mapItemToDto(item);
    }

    @Override
    public List<ItemDto> findByOwnerId(long ownerId) {
        userRepository.validateUserExists(ownerId);
        return itemRepository.findByOwnerId(ownerId)
                .stream()
                .map(ItemMapper::mapItemToDto)
                .toList();
    }

    @Override
    public List<ItemDto> findByQuery(String text) {
        return itemRepository.findByQuery(text)
                .stream()
                .map(ItemMapper::mapItemToDto)
                .toList();
    }

    @Override
    public void delete(long itemId, long ownerId) {
        itemRepository.validateItemOwner(itemId, ownerId);
        itemRepository.delete(itemId);
    }
}
