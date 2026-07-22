package ru.nateemaru.polygon.dto.request;

import jakarta.validation.constraints.NotBlank;
import ru.nateemaru.polygon.dto.command.CreateDepartmentCommand;

public record CreateDepartmentRequest(
        @NotBlank
        String name) {

        public CreateDepartmentCommand toCommand() {
                return new CreateDepartmentCommand(name);
        }
}
