package ru.nateemaru.polygon.dto.projection;

import ru.nateemaru.polygon.entity.EmployeePosition;

public record EmployeeProjection(String fullName, EmployeePosition position, String departmentName) {
}
