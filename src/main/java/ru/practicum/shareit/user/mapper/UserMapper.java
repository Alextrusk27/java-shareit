package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public class UserMapper {
    public static UserDto mapUserToDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    public static User mapCreateRequestToUser(CreateUserRequest createRequest) {
        return User.builder()
                .name(createRequest.name())
                .email(createRequest.email())
                .build();
    }

    public static User mapUpdateRequestToUser(UpdateUserRequest updateRequest) {
        User result = User.builder().build();

        if (updateRequest.hasName()) {
            result.setName(updateRequest.name());
        }
        if (updateRequest.hasEmail()) {
            result.setEmail(updateRequest.email());
        }
        return result;
    }
}