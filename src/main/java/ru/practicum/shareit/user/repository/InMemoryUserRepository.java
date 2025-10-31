package ru.practicum.shareit.user.repository;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.DuplicateException;
import ru.practicum.shareit.user.model.User;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

@Getter
@Repository
@RequiredArgsConstructor
public class InMemoryUserRepository implements UserRepository {

    private final Map<Long, User> users;
    private final Set<String> emails;
    private final AtomicLong idGenerator;

    @Override
    public void save(User user) {
        throwIfEmailExists(user.getEmail());
        user.setId(idGenerator.getAndIncrement());
        users.put(user.getId(), user);
        emails.add(user.getEmail());
    }

    @Override
    public void update(User user, long id) {
        User existingUser = users.get(id);
        String newEmail = user.getEmail();
        String oldEmail = existingUser.getEmail();

        if (newEmail != null && !newEmail.isEmpty()) {
            if (!oldEmail.equals(newEmail)) {
                throwIfEmailExists(newEmail);
                existingUser.setEmail(newEmail);
                emails.remove(oldEmail);
                emails.add(newEmail);
            }
        }
        String name = user.getName();

        if (name != null && !name.isEmpty()) {
            existingUser.setName(name);
        }
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

    private void throwIfEmailExists(String email) {
        if (emails.contains(email)) {
            throw new DuplicateException("Email %s already exists".formatted(email));
        }
    }
}