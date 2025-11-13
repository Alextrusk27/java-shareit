package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;

@Mapper
public interface ItemMapper {
    ItemDto toItemDto(Item item);

    @Mapping(target = "id", ignore = true)
    Item toItemFromCreate(CreateItemRequest createRequest, long userId);

    Item toItemFromUpdate(UpdateItemRequest updateRequest, long id, long userId);
}
