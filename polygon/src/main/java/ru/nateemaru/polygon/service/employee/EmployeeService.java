package ru.nateemaru.polygon.service.employee;

import ru.nateemaru.polygon.dto.command.CreateEmployeeCommand;
import ru.nateemaru.polygon.dto.command.UpdateEmployeeCommand;
import ru.nateemaru.polygon.dto.response.EmployeeDto;
import ru.nateemaru.polygon.dto.response.EmployeePreviewDto;

public interface EmployeeService {
    EmployeeDto create(CreateEmployeeCommand command);
    EmployeePreviewDto getPreviewById(Long id);
    EmployeeDto updateById(Long id, UpdateEmployeeCommand command);
    void deleteById(Long id);
}
