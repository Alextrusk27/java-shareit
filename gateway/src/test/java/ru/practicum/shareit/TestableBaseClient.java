package ru.practicum.shareit;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

public class TestableBaseClient extends BaseClient {
    public TestableBaseClient(RestTemplate rest) {
        super(rest);
    }

    @Override
    public ResponseEntity<Object> get(String path) {
        return super.get(path);
    }

    @Override
    public ResponseEntity<Object> get(String path, long userId) {
        return super.get(path, userId);
    }

    @Override
    public ResponseEntity<Object> get(String path, Long userId, Map<String, Object> parameters) {
        return super.get(path, userId, parameters);
    }

    @Override
    public <T> ResponseEntity<Object> post(String path, T body) {
        return super.post(path, body);
    }

    @Override
    public <T> ResponseEntity<Object> post(String path, long userId, T body) {
        return super.post(path, userId, body);
    }

    @Override
    public <T> ResponseEntity<Object> post(String path, Long userId, Map<String, Object> parameters, T body) {
        return super.post(path, userId, parameters, body);
    }

    @Override
    public <T> ResponseEntity<Object> put(String path, long userId, T body) {
        return super.put(path, userId, body);
    }

    @Override
    public <T> ResponseEntity<Object> put(String path, long userId, Map<String, Object> parameters, T body) {
        return super.put(path, userId, parameters, body);
    }

    @Override
    public <T> ResponseEntity<Object> patch(String path, T body) {
        return super.patch(path, body);
    }

    @Override
    public <T> ResponseEntity<Object> patch(String path, long userId) {
        return super.patch(path, userId);
    }

    @Override
    public <T> ResponseEntity<Object> patch(String path, long userId, T body) {
        return super.patch(path, userId, body);
    }

    @Override
    public <T> ResponseEntity<Object> patch(String path, Long userId, Map<String, Object> parameters, T body) {
        return super.patch(path, userId, parameters, body);
    }

    @Override
    public ResponseEntity<Object> delete(String path) {
        return super.delete(path);
    }

    @Override
    public ResponseEntity<Object> delete(String path, long userId) {
        return super.delete(path, userId);
    }

    @Override
    public ResponseEntity<Object> delete(String path, Long userId, Map<String, Object> parameters) {
        return super.delete(path, userId, parameters);
    }
}
