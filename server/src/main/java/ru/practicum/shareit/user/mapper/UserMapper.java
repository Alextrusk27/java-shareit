package ru.practicum.shareit.user.mapper;

import org.mapstruct.*;
import ru.practicum.shareit.user.dto.*;
import ru.practicum.shareit.user.model.User;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    UserDto toUserDto(User user);

    User toUserFromCreate(CreateUserRequest createRequest);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUser(UpdateUserRequest updateRequest, @MappingTarget User user);
}
