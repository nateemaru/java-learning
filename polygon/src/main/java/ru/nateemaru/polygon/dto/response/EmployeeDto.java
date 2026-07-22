package ru.nateemaru.polygon.dto.response;

import ru.nateemaru.polygon.entity.EmployeePosition;

public record EmployeeDto(
        Long id,
        String firstName,
        String lastName,
        EmployeePosition position,
        Integer salary,
        Long departmentId) {
}
