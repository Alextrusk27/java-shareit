package ru.practicum.shareit.sharing;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.NotFoundException;

@Component
public class EntityFinder {

    public <T> T findOrThrow(JpaRepository<T, Long> repository, Long id, EntityType entityType) {
        return repository.findById(id)
                .orElseThrow(() ->  new NotFoundException("%s id=%d not found".formatted(entityType.getName(), id)));
    }
}
