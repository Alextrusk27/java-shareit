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
    private final ItemMapper itemMapper;
    private final InMemoryItemRepository itemRepository;
    private final InMemoryUserRepository userRepository;

    @Override
    public ItemDto create(CreateItemRequest createRequest, long userId) {
        userRepository.validateUserExists(userId);
        Item newItem = itemMapper.toItemFromCreate(createRequest, userId);
        newItem = itemRepository.save(newItem);
        return itemMapper.toItemDto(newItem);
    }

    @Override
    public ItemDto update(UpdateItemRequest updateRequest, long id, long userId) {
        userRepository.validateUserExists(userId);
        itemRepository.validateItemOwner(id, userId);
        Item updateItem = itemMapper.toItemFromUpdate(updateRequest, id, userId);
        updateItem = itemRepository.update(updateItem);
        return itemMapper.toItemDto(updateItem);
    }

    @Override
    public ItemDto findById(long id) {
        Item item = itemRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Item id=%d not found".formatted(id)));
        return itemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> findByOwnerId(long userId) {
        userRepository.validateUserExists(userId);
        return itemRepository.findByOwnerId(userId)
                .stream()
                .map(itemMapper::toItemDto)
                .toList();
    }

    @Override
    public List<ItemDto> findByQuery(String text) {
        return itemRepository.findByQuery(text)
                .stream()
                .map(itemMapper::toItemDto)
                .toList();
    }

    @Override
    public void delete(long itemId, long userId) {
        itemRepository.validateItemOwner(itemId, userId);
        itemRepository.delete(itemId);
    }
}
