package ru.nateemaru.polygon.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.jspecify.annotations.Nullable;
import ru.nateemaru.polygon.dto.command.UpdateEmployeeCommand;
import ru.nateemaru.polygon.entity.EmployeePosition;

public record UpdateEmployeeRequest(
        @Nullable
        @Pattern(regexp = ".*\\S.*")
        String firstName,
        @Nullable
        @Pattern(regexp = ".*\\S.*")
        String lastName,
        @Nullable
        EmployeePosition position,
        @Nullable
        @PositiveOrZero
        Integer salary,
        @Nullable
        @Positive
        Long departmentId) {

    public UpdateEmployeeCommand toCommand() {
        return new UpdateEmployeeCommand(firstName, lastName, position, salary, departmentId);
    }
}
