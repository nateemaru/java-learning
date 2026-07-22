package ru.nateemaru.polygon.dto.command;

import ru.nateemaru.polygon.entity.EmployeePosition;

public record CreateEmployeeCommand(
        String firstName,
        String lastName,
        EmployeePosition position,
        Integer salary,
        Long departmentId) {
}
