package ru.practicum.shareit.user.repository;

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
    private final AtomicLong idGenerator;

    @Override
    public void save(User user) {
        validateEmailExists(user.getEmail());
        user.setId(idGenerator.getAndIncrement());
        users.put(user.getId(), user);
        emails.add(user.getEmail());
    }

    @Override
    public void update(User user, long id) {
        User existing = users.get(id);

        Optional.ofNullable(user.getName())
                .filter(name -> !name.isBlank())
                .ifPresent(existing::setName);

        Optional.ofNullable(user.getEmail())
                .filter(email -> !email.isBlank())
                .filter(email -> !email.equals(existing.getEmail()))
                .ifPresent(newEmail -> {
                    validateEmailExists(newEmail);
                    emails.remove(existing.getEmail());
                    existing.setEmail(newEmail);
                    emails.add(newEmail);
                });
    }

    @Override
    public void delete(long id) {
        String email = users.get(id).getEmail();
        emails.remove(email);
        users.remove(id);
    }

    @Override
    public Optional<User> findById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    private void validateEmailExists(String email) {
        if (emails.contains(email)) {
            throw new DuplicateException("Email %s already exists".formatted(email));
        }
    }

    public void validateUserExists(long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("User with ID %d not found".formatted(id));
        }
    }
}