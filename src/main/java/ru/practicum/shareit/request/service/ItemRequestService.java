package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestExtendedDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.creation.CreateItemRequest;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto create(Long authorId, CreateItemRequest itemRequest);

    List<ItemRequestExtendedDto> getAllByAuthor(Long authorId);

    List<ItemRequestDto> getAll();

    ItemRequestExtendedDto getById(Long id);
}
