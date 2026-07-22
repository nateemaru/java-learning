package ru.nateemaru.polygon.dto.request;

import org.jspecify.annotations.Nullable;
import ru.nateemaru.polygon.dto.command.UpdateDepartmentCommand;

public record UpdateDepartmentRequest(
        @Nullable
        String name) {

    public UpdateDepartmentCommand toCommand() {
        return new UpdateDepartmentCommand(name);
    }
}
