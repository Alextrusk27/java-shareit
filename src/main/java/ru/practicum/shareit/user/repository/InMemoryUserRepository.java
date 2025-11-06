package ru.practicum.shareit.user.repository;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.DuplicateException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@RequiredArgsConstructor
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> users;
    private final Set<String> emails;
    private final AtomicLong userIdGenerator;

    @Override
    public User save(User user) {
        validateEmailExists(user.getEmail());
        user.setId(userIdGenerator.incrementAndGet());
        users.put(user.getId(), user);
        emails.add(user.getEmail());
        return user;
    }

    @Override
    public User update(User user, long userId) {
        User existingUser = findById(userId);

        Optional.ofNullable(user.getName())
                .filter(name -> !name.isBlank())
                .ifPresent(existingUser::setName);

        Optional.ofNullable(user.getEmail())
                .filter(email -> !email.isBlank())
                .filter(email -> !email.equals(existingUser.getEmail()))
                .ifPresent(newEmail -> {
                    validateEmailExists(newEmail);
                    emails.remove(existingUser.getEmail());
                    existingUser.setEmail(newEmail);
                    emails.add(newEmail);
                });
        return existingUser;
    }

    @Override
    public void delete(long userId) {
        String email = findById(userId).getEmail();
        emails.remove(email);
        users.remove(userId);
    }

    @Override
    public User findById(long userId) {
        return Optional.ofNullable(users.get(userId))
                .orElseThrow(() -> new NotFoundException("User with ID %d not found".formatted(userId)));
    }

    private void validateEmailExists(String email) {
        if (emails.contains(email)) {
            throw new DuplicateException("Email %s already exists".formatted(email));
        }
    }

    public void validateUserExists(long userId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("User with ID %d not found".formatted(userId));
        }
    }

    protected void clear() {
        users.clear();
        emails.clear();
        userIdGenerator.set(0);
    }
}