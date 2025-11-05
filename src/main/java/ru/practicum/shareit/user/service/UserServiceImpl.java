package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.InMemoryUserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final InMemoryUserRepository userRepository;

    @Override
    public UserDto create(User user) {
        userRepository.save(user);
        return findById(user.getId());
    }

    @Override
    public UserDto update(User user, long userId) {
        userRepository.validateUserExists(userId);
        userRepository.update(user, userId);
        return findById(userId);
    }

    @Override
    public void delete(long userId) {
        userRepository.validateUserExists(userId);
        log.info("User id={} was deleted", userId);
        userRepository.delete(userId);
    }

    @Override
    public UserDto findById(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User with ID %d not found".formatted(userId)));
        return UserMapper.mapUserToDto(user);
    }
}