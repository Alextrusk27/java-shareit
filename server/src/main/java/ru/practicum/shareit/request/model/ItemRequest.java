package ru.practicum.shareit.request.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "requests")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    User author;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime created;

    @ManyToMany(mappedBy = "itemRequests", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Item> items = new ArrayList<>();
}
