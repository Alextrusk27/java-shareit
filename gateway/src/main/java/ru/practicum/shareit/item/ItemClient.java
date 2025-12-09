package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CreateComment;
import ru.practicum.shareit.item.dto.CreateItem;
import ru.practicum.shareit.item.dto.UpdateItem;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> create(long userId, CreateItem createRequest) {
        return post("", userId, createRequest);
    }

    public ResponseEntity<Object> update(long userId, long id, UpdateItem updateRequest) {
        return patch("/" + id, userId, updateRequest);
    }

    public ResponseEntity<Object> getById(long userId, long id) {
        return get("/" + id, userId);
    }

    public ResponseEntity<Object> getAll(long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> getByQuery(long userId, String text) {
        Map<String, Object> params = Map.of(
                "text", text
        );
        return get("/search?text={text}", userId, params);
    }

    public ResponseEntity<Object> delete(long userId, long id) {
        return delete("/" + id, userId);
    }

    public ResponseEntity<Object> createComment(long userId, long itemId, CreateComment commentRequest) {
        return post("/" + itemId + "/comment", userId, commentRequest);
    }
}
