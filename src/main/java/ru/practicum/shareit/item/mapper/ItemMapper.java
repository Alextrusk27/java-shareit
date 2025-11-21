package ru.practicum.shareit.item.mapper;

import org.mapstruct.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ItemMapper {
    ItemDto toItemDto(Item item);

    Item toItemFromCreate(CreateItemRequest createRequest);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateItem(UpdateItemRequest updateRequest, @MappingTarget Item item);
}
