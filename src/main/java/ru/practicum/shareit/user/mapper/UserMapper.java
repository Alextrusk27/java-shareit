package ru.practicum.shareit.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.user.dto.*;
import ru.practicum.shareit.user.model.User;

@Mapper
public interface UserMapper {
    UserDto toUserDto(User user);

    @Mapping(target = "id", ignore = true)
    User toUserFromCreate(CreateUserRequest createRequest);

    User toUserFromUpdate(UpdateUserRequest updateRequest, long id);
}
