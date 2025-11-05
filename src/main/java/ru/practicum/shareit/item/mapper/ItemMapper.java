package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Item;

public class ItemMapper {
    public static ItemDto mapItemToDto(Item item) {
        return new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable());
    }

    public static Item mapCreateRequestToItem(CreateItemRequest createRequest, long ownerId) {
        return Item.builder()
                .name(createRequest.name())
                .description(createRequest.description())
                .available(createRequest.available())
                .ownerId(ownerId)
                .build();
    }

    public static Item mapUpdateRequestToItem(UpdateItemRequest updateRequest, long itemId) {
        Item result = Item.builder()
                .id(itemId)
                .build();

        if (updateRequest.hasName()) {
            result.setName(updateRequest.name());
        }
        if (updateRequest.hasDescription()) {
            result.setDescription(updateRequest.description());
        }
        if (updateRequest.hasAvailable()) {
            result.setAvailable(updateRequest.available());
        }
        return result;
    }
}
