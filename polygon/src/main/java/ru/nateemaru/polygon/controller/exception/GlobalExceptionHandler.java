package ru.nateemaru.polygon.controller.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.nateemaru.polygon.exception.BookNotFoundException;
import ru.nateemaru.polygon.exception.FieldValidationError;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    private final static String TIMESTAMP = "TIMESTAMP";

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleBookNotFoundException(BookNotFoundException exception, HttpServletRequest request) {
        log.warn("Book with id={} not found", exception.getId());
        HttpStatus status = HttpStatus.NOT_FOUND;

        ProblemDetail detail = ProblemDetail.forStatus(status);
        detail.setInstance(URI.create(request.getRequestURI()));
        detail.setTitle(exception.getClass().getName());
        detail.setProperty(TIMESTAMP, LocalDateTime.now());

        return ResponseEntity
                .status(status)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(detail);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException exception, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        ProblemDetail detail = ProblemDetail.forStatus(status);
        detail.setInstance(URI.create(request.getRequestURI()));
        detail.setTitle(exception.getClass().getName());
        detail.setProperty(TIMESTAMP, LocalDateTime.now());
        detail.setDetail(exception.getMessage());

        return ResponseEntity
                .status(status)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(detail);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ProblemDetail> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException exception, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String fieldName = exception.getName();
        Object fieldValue = exception.getValue();
        String expectedType = exception.getRequiredType() != null ? exception.getRequiredType().toString() : "unknown";
        String message = String.format("%s field requires a %s, but received a %s", fieldName, expectedType, fieldValue);

        List<FieldValidationError> errors = List.of(
                new FieldValidationError(fieldName, message)
        );

        ProblemDetail detail = ProblemDetail.forStatus(status);
        detail.setInstance(URI.create(request.getRequestURI()));
        detail.setTitle(exception.getClass().getName());
        detail.setProperty(TIMESTAMP, LocalDateTime.now());
        detail.setDetail(errors.toString());

        return ResponseEntity
                .status(status)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(detail);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException exception, HttpServletRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST;

        List<FieldValidationError> validationErrors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> {
                    String message = error.getDefaultMessage() != null ? error.getDefaultMessage() : "";
                    return new FieldValidationError(error.getField(), message);
                })
                .toList();


        ProblemDetail detail = ProblemDetail.forStatus(status);
        detail.setInstance(URI.create(request.getRequestURI()));
        detail.setTitle(exception.getClass().getName());
        detail.setProperty(TIMESTAMP, LocalDateTime.now());
        detail.setDetail(validationErrors.toString());

        return ResponseEntity
                .status(status)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(detail);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGeneric(Exception exception, HttpServletRequest request) {
        if (exception instanceof ErrorResponse errorResponse) {
            HttpStatus status = HttpStatus.valueOf(errorResponse.getStatusCode().value());

            ProblemDetail detail = ProblemDetail.forStatusAndDetail(
                    HttpStatus.valueOf(errorResponse.getStatusCode().value()),
                    exception.getMessage());
            detail.setInstance(URI.create(request.getRequestURI()));
            detail.setTitle(exception.getClass().getName());
            detail.setProperty(TIMESTAMP, LocalDateTime.now());

            return ResponseEntity
                    .status(status)
                    .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                    .body(detail);
        }

        log.error("Not expected exception {}. Message: {}", exception.getClass().getTypeName(), exception.getMessage());

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        ProblemDetail detail = ProblemDetail.forStatusAndDetail(status, exception.getMessage());
        detail.setInstance(URI.create(request.getRequestURI()));
        detail.setTitle(exception.getClass().getName());
        detail.setProperty(TIMESTAMP, LocalDateTime.now());

        return ResponseEntity
                .status(status)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(detail);
    }
}
