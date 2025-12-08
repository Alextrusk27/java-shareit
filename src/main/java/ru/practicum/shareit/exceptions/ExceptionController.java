package ru.practicum.shareit.exceptions;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Collections;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler({
            ConstraintViolationException.class,
            MethodArgumentNotValidException.class,
            MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<ErrorResponse> handleValidationExceptions(final Exception e) {
        List<String> errors = switch (e) {
            case ConstraintViolationException cve -> cve.getConstraintViolations().stream()
                    .map(ConstraintViolation::getMessage)
                    .toList();
            case MethodArgumentNotValidException mnv -> mnv.getBindingResult().getFieldErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            case MethodArgumentTypeMismatchException mtm -> {
                String typeName = mtm.getRequiredType() != null ?
                        mtm.getRequiredType().getName() : "unknown";
                yield List.of("Parameter '%s' should be of type %s".formatted(mtm.getName(), typeName));
            }
            default -> List.of(e.getMessage());
        };
        log.info("Validation exception: {}", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Validation Failed", errors));
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ErrorResponse> handleMissingRequestHeaderExceptions(final MissingRequestHeaderException e) {
        log.info("Missing headers exception: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Missing required headers: '%s'", List.of(e.getHeaderName())));
    }

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
