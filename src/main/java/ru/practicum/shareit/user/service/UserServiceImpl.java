package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.DuplicateException;
import ru.practicum.shareit.sharing.EntityFinder;
import ru.practicum.shareit.sharing.EntityType;
import ru.practicum.shareit.user.dto.*;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final EntityFinder entityFinder;

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public UserDto create(CreateUserRequest createRequest) {
        checkEmailUniqueness(createRequest.email());
        User newUser = userMapper.toUserFromCreate(createRequest);
        newUser = userRepository.save(newUser);
        return userMapper.toUserDto(newUser);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public UserDto update(UpdateUserRequest updateRequest, long id) {
        User existingUser = entityFinder.findOrThrow(userRepository, id, EntityType.USER);

        if (updateRequest.email() != null &&
                !updateRequest.email().isBlank() &&
                !updateRequest.email().equals(existingUser.getEmail())) {
            checkEmailUniqueness(updateRequest.email());
        }

        userMapper.updateUser(updateRequest, existingUser);
        User updatedUser = userRepository.save(existingUser);
        return userMapper.toUserDto(updatedUser);
    }

    @Override
    @Transactional
    public void delete(long id) {
        User user = entityFinder.findOrThrow(userRepository, id, EntityType.USER);
        userRepository.delete(user);
    }

    @Override
    public UserDto findById(long id) {
        User user = entityFinder.findOrThrow(userRepository, id, EntityType.USER);
        return userMapper.toUserDto(user);
    }

    private void checkEmailUniqueness(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateException("Email %s already exists".formatted(email));
        }
    }
}
