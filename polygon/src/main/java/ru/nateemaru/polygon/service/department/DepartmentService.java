package ru.nateemaru.polygon.service.department;

import ru.nateemaru.polygon.dto.command.CreateDepartmentCommand;
import ru.nateemaru.polygon.dto.command.UpdateDepartmentCommand;
import ru.nateemaru.polygon.dto.response.DepartmentDto;

public interface DepartmentService {
    DepartmentDto create(CreateDepartmentCommand command);
    DepartmentDto getById(Long id);
    DepartmentDto updateById(Long id, UpdateDepartmentCommand command);
    void deleteById(Long id);
}
