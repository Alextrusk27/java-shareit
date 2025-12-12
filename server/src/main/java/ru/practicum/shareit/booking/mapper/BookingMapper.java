package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfo;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.mapper.UserMapper;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {ItemMapper.class, UserMapper.class})
public interface BookingMapper {

    BookingDto toBookingDto(Booking booking);

    @Mapping(target = "bookerId", source = "booker.id")
    BookingInfo toBookingInfo(Booking booking);

    Booking toBookingFromCreate(CreateBookingRequest createRequest);
}
