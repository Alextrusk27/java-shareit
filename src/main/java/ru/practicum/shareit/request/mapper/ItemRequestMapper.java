package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.creation.CreateItemRequest;
import ru.practicum.shareit.request.model.ItemRequest;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ItemRequestMapper {

    ItemRequest toItem(CreateItemRequest createRequest);

    ItemRequestDto toItemRequestDto(ItemRequest itemRequest);
}
