package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.dto.projection.CommentWithItemIdProjection;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("""
            SELECT
                c.id as id,
                c.author.name as authorName,
                c.text as text,
                c.created as created,
                c.item.id as itemId
            FROM Comment c
            WHERE c.item.id IN :itemIds
            ORDER BY c.created DESC
            """)
    List<CommentWithItemIdProjection> findByItemIds(@Param("itemIds") List<Long> itemIds);

    List<Comment> findByItemId(Long itemId);
}
