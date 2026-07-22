package ru.nateemaru.polygon.exception;

import lombok.Getter;

public class DepartmentNotFoundException extends RuntimeException {
    @Getter
    private final Long id;

    public DepartmentNotFoundException(Long id) {
        this.id = id;
    }
}
