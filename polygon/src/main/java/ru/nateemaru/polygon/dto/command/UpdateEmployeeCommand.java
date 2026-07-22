package ru.nateemaru.polygon.dto.command;

import ru.nateemaru.polygon.entity.EmployeePosition;

public record UpdateEmployeeCommand(
        String firstName,
        String lastName,
        EmployeePosition position,
        Integer salary,
        Long departmentId) {
}
