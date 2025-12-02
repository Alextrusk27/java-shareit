package ru.practicum.shareit.item.mapper;

import org.mapstruct.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExtendedDto;
import ru.practicum.shareit.item.dto.projection.ItemWithBookingProjection;
import ru.practicum.shareit.item.dto.request.CreateItemRequest;
import ru.practicum.shareit.item.dto.request.UpdateItemRequest;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ItemMapper {
    ItemDto toItemDto(Item item);

    Item toItemFromCreate(CreateItemRequest createRequest);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateItem(UpdateItemRequest updateRequest, @MappingTarget Item item);

    @Mapping(target = "lastBooking", source = "lastBookingInfo")
    @Mapping(target = "nextBooking", source = "nextBookingInfo")
    @Mapping(target = "comments", ignore = true)
    ItemExtendedDto toExtendedDto(ItemWithBookingProjection projection);

    default ItemExtendedDto toExtendedDtoWithComments(ItemWithBookingProjection projection,
                                                      List<CommentDto> comments) {
        ItemExtendedDto dto = toExtendedDto(projection);
        return new ItemExtendedDto(dto.id(), dto.name(), dto.description(), dto.available(),
                dto.lastBooking(), dto.nextBooking(), comments != null ? comments : List.of());
    }
}
