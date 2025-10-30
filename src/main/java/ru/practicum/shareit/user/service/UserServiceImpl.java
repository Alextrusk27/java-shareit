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

    private final InMemoryUserRepository userStorage;

    @Override
    public UserDto create(User user) {
        userStorage.save(user);
        log.info("User created: {}", user);
        return findById(user.getId());
    }

    @Override
    public UserDto update(User user, long id) {
        throwIfUserNotExists(id);
        userStorage.update(user, id);
        UserDto userDto = findById(id);
        log.info("User updated: {}", userDto);
        return userDto;
    }

    @Override
    public void delete(long id) {
        throwIfUserNotExists(id);
        log.info("User deleted: ID{}", id);
        userStorage.delete(id);
    }

    @Override
    public UserDto findById(long id) {
        User user = userStorage.findById(id).orElseThrow(() ->
                new NotFoundException("User with ID %d not found".formatted(id)));
        log.debug("User found: {}", user);
        return UserMapper.mapUserToDto(user);
    }

    private void throwIfUserNotExists(long id) {
        if (!userStorage.getUsers().containsKey(id)) {
            throw new NotFoundException("User with ID %d not found".formatted(id));
        }
    }
}