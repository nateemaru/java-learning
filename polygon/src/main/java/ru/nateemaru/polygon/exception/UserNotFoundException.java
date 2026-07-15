package ru.nateemaru.polygon.exception;

import lombok.Getter;

public class UserNotFoundException extends RuntimeException {
    @Getter
    private final Long id;

    public UserNotFoundException(Long id) {
        this.id = id;
    }
}
