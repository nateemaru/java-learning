package ru.nateemaru.polygon.exception;

import lombok.Getter;

public class EmployeeNotFoundException extends RuntimeException {
    @Getter
    private final Long id;

    public EmployeeNotFoundException(Long id) {
        this.id = id;
    }
}
