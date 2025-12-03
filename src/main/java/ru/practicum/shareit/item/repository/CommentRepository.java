package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("""
            SELECT c
            FROM Comment c
            WHERE c.item.id IN :itemIds
            ORDER BY c.created DESC
            """)
    List<Comment> findByItemIds(List<Long> itemIds);

    List<Comment> findByItemId(Long itemId);
}
