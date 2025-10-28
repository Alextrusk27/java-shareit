package ru.practicum.shareit.user.repository;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.common.exceptions.DuplicateException;
import ru.practicum.shareit.user.model.User;

import java.util.HashSet;
import java.util.Optional;
import java.util.TreeMap;

@Getter
@Repository
@RequiredArgsConstructor
public class InMemoryUserRepository implements UserRepository {

    private final TreeMap<Long, User> users;
    private final HashSet<String> emails;

    @Override
    public void save(User user) {
        throwIfEmailExists(user.getEmail());
        user.setId(generateId());
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

    private long generateId() {
        long id;
        if (!emails.isEmpty()) {
            id = users.lastKey();
        } else {
            id = 0;
        }
        return ++id;
    }

    private void throwIfEmailExists(String email) {
        if (emails.contains(email)) {
            throw new DuplicateException("Email %s already exists".formatted(email));
        }
    }
}