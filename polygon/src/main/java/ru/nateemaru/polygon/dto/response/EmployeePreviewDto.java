package ru.nateemaru.polygon.dto.response;

import ru.nateemaru.polygon.entity.EmployeePosition;

public record EmployeePreviewDto(String fullName, EmployeePosition position, String departmentName) {
}
