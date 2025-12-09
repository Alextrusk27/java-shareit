package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

public record ItemRequestExtendedDto(Long id,
                                     String description,
                                     LocalDateTime created,
                                     List<ItemDto> items) {
}
