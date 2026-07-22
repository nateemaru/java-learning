package ru.nateemaru.polygon.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.jspecify.annotations.Nullable;
import ru.nateemaru.polygon.dto.command.CreateEmployeeCommand;
import ru.nateemaru.polygon.entity.EmployeePosition;

public record CreateEmployeeRequest(
        @NotBlank
        String firstName,
        @NotBlank
        String lastName,
        @NotNull
        EmployeePosition position,
        @NotNull
        @PositiveOrZero
        Integer salary,
        @Nullable
        @Positive
        Long departmentId) {

        public CreateEmployeeCommand toCommand() {
                return new CreateEmployeeCommand(firstName, lastName, position, salary, departmentId);
        }
}
