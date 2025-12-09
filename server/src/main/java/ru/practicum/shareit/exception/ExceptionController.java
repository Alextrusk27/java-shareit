package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;

@Slf4j
@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateException(final DuplicateException e) {
        log.info("Duplicate exception: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("Duplicate data found", Collections.singletonList(e.getMessage())));
    }

    @ExceptionHandler(UnavailableException.class)
    public ResponseEntity<ErrorResponse> handleUnavailableException(final UnavailableException e) {
        log.info("Unavailable exception: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Resource unavailable", Collections.singletonList(e.getMessage())));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(final NotFoundException e) {
        log.info("NotFound exception: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("Resource not found", Collections.singletonList(e.getMessage())));
    }

    @ExceptionHandler(OwnershipException.class)
    public ResponseEntity<ErrorResponse> handleItemOwnershipException(final OwnershipException e) {
        log.info("ItemOwnershipException exception: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse("Forbidden", Collections.singletonList(e.getMessage())));
    }
}
