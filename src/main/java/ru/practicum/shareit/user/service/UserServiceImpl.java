package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.InMemoryItemRepository;
import ru.practicum.shareit.user.dto.*;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.InMemoryUserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final InMemoryUserRepository userRepository;
    private final InMemoryItemRepository itemRepository;

    @Override
    public UserDto create(CreateUserRequest createRequest) {
        User newUser = userMapper.toUserFromCreate(createRequest);
        newUser = userRepository.save(newUser);
        return userMapper.toUserDto(newUser);
    }

    @Override
    public UserDto update(UpdateUserRequest updateRequest, long id) {
        User updateUser = userMapper.toUserFromUpdate(updateRequest, id);
        updateUser = userRepository.update(updateUser);
        return userMapper.toUserDto(updateUser);
    }

    @Override
    public void delete(long id) {
        List<Item> userItems = itemRepository.findByOwnerId(id);
        userItems.forEach(item -> itemRepository.delete(item.getId()));
        userRepository.delete(id);
    }

    @Override
    public UserDto findById(long id) {
        User user = userRepository.findById(id);
        return userMapper.toUserDto(user);
    }
}