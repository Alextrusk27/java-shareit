package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.*;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.InMemoryUserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final InMemoryUserRepository userRepository;

    @Override
    public UserDto create(CreateUserRequest createRequest) {
        User newUser = UserMapper.mapCreateRequestToUser(createRequest);
        newUser = userRepository.save(newUser);
        return UserMapper.mapUserToDto(newUser);
    }

    @Override
    public UserDto update(UpdateUserRequest updateRequest, long userId) {
        User updateUser = UserMapper.mapUpdateRequestToUser(updateRequest);
        updateUser = userRepository.update(updateUser, userId);
        return UserMapper.mapUserToDto(updateUser);
    }

    @Override
    public void delete(long userId) {
        userRepository.delete(userId);
    }

    @Override
    public UserDto findById(long userId) {
        User user = userRepository.findById(userId);
        return UserMapper.mapUserToDto(user);
    }
}