package ru.practicum.shareit.item.mapper;

import org.mapstruct.*;
import ru.practicum.shareit.booking.dto.BookingInfo;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExtendedDto;
import ru.practicum.shareit.item.dto.request.CreateItem;
import ru.practicum.shareit.item.dto.request.UpdateItem;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ItemMapper {
    ItemDto toItemDto(Item item);

    Item toItemFromCreate(CreateItem createRequest);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateItem(UpdateItem updateRequest, @MappingTarget Item item);

    default ItemExtendedDto toExtendedDto(Item item,
                                          BookingInfo lastBooking,
                                          BookingInfo nextBooking,
                                          List<CommentDto> comments) {
        return new ItemExtendedDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                lastBooking,
                nextBooking,
                comments);
    }
}
