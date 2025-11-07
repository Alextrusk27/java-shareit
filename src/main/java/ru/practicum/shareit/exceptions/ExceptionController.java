package ru.practicum.shareit.exceptions;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Collections;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class ExceptionController {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
            ConstraintViolationException.class,
            MethodArgumentNotValidException.class,
            MethodArgumentTypeMismatchException.class
    })
    public ErrorResponse handleValidationExceptions(final Exception e) {
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
        return new ErrorResponse("Validation Failed", errors);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(MissingRequestHeaderException.class)
    public ErrorResponse handleMissingRequestHeaderExceptions(final MissingRequestHeaderException e) {
        log.info("Missing headers exception: {}", e.getMessage());
        return new ErrorResponse("Missing required headers: '%s'", List.of(e.getHeaderName()));
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DuplicateException.class)
    public ErrorResponse handleDuplicateException(final DuplicateException e) {
        log.info("Duplicate exception: {}", e.getMessage());
        return new ErrorResponse("Duplicate data found", Collections.singletonList(e.getMessage()));
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ErrorResponse handleNotFoundException(final NotFoundException e) {
        log.info("NotFound exception: {}", e.getMessage());
        return new ErrorResponse("Not found", Collections.singletonList(e.getMessage()));
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(ItemOwnershipException.class)
    public ErrorResponse handleItemOwnershipException(final ItemOwnershipException e) {
        log.info("ItemOwnershipException exception: {}", e.getMessage());
        return new ErrorResponse("Ownerships conflict", Collections.singletonList(e.getMessage()));
    }
}
