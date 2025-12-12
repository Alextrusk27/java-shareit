package ru.practicum.shareit;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.shareit.exception.ExceptionController;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ExceptionControllerTest {

    private final ExceptionController controller = new ExceptionController();

    @Test
    void testAllExceptions() throws NoSuchMethodException {
        ExceptionController c = new ExceptionController();

        ConstraintViolation<?> v = mock(ConstraintViolation.class);
        when(v.getMessage()).thenReturn("Error");
        assertEquals(HttpStatus.BAD_REQUEST, c.handleValidationExceptions(
                new ConstraintViolationException(Set.of(v))).getStatusCode());

        BindingResult br = mock(BindingResult.class);
        when(br.getFieldErrors()).thenReturn(List.of(new FieldError("o", "f", "Error")));
        assertEquals(HttpStatus.BAD_REQUEST, c.handleValidationExceptions(
                new MethodArgumentNotValidException(null, br)).getStatusCode());

        MethodArgumentTypeMismatchException m = mock(MethodArgumentTypeMismatchException.class);
        when(m.getRequiredType()).thenReturn((Class) Long.class);
        when(m.getName()).thenReturn("id");
        assertEquals(HttpStatus.BAD_REQUEST, c.handleValidationExceptions(m).getStatusCode());

        Method method = String.class.getMethod("toString");
        MethodParameter parameter = new MethodParameter(method, -1);
        MissingRequestHeaderException mrhe = new MissingRequestHeaderException("X-Header", parameter);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,
                c.handleMissingRequestHeaderExceptions(mrhe).getStatusCode());
    }
}