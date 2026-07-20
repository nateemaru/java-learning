package ru.nateemaru.polygon.exception;

import lombok.Getter;

public class BookNotFoundException extends RuntimeException {
    @Getter
    private final Long id;

    public BookNotFoundException(Long id) {
        this.id = id;
    }
}
