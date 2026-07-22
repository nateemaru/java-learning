package ru.nateemaru.polygon.exception;

import lombok.Getter;

public class DepartmentAlreadyExistsException extends RuntimeException{
    @Getter
    private final String name;

    public DepartmentAlreadyExistsException(String name) {
        this.name = name;
    }
}
