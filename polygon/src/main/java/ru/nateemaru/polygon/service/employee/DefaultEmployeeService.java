package ru.nateemaru.polygon.service.employee;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.nateemaru.polygon.dto.command.CreateEmployeeCommand;
import ru.nateemaru.polygon.dto.command.UpdateEmployeeCommand;
import ru.nateemaru.polygon.dto.projection.EmployeeProjection;
import ru.nateemaru.polygon.dto.response.EmployeeDto;
import ru.nateemaru.polygon.dto.response.EmployeePreviewDto;
import ru.nateemaru.polygon.entity.Employee;
import ru.nateemaru.polygon.exception.EmployeeNotFoundException;
import ru.nateemaru.polygon.mapping.EmployeeMapper;
import ru.nateemaru.polygon.repository.EmployeeRepository;

@Service
@RequiredArgsConstructor
public class DefaultEmployeeService implements EmployeeService {
    private final EmployeeRepository repository;
    private final EmployeeMapper mapper;

    @Override
    public EmployeeDto create(CreateEmployeeCommand command) {
        Employee employee = new Employee(
                null,
                command.firstName(),
                command.lastName(),
                command.position(),
                command.salary(),
                command.departmentId()
        );

        Employee saved = repository.save(employee);
        return mapper.toDto(saved);
    }

    @Override
    public EmployeePreviewDto getPreviewById(Long id) {
        EmployeeProjection projection = repository.findProjectionById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));

        return mapper.toDto(projection);
    }

    @Override
    public EmployeeDto updateById(Long id, UpdateEmployeeCommand command) {
        Employee employee = repository.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));

        if (command.firstName() != null) {
            employee.setFirstName(command.firstName());
        }

        if (command.lastName() != null) {
            employee.setLastName(command.lastName());
        }

        if (command.position() != null) {
            employee.setPosition(command.position());
        }

        if (command.salary() != null) {
            employee.setSalary(command.salary());
        }

        if (command.departmentId() != null) {
            employee.setDepartmentId(command.departmentId());
        }

        Employee saved = repository.save(employee);
        return mapper.toDto(saved);
    }

    @Override
    public void deleteById(Long id) {
        int result = repository.deleteAndCountById(id);

        if (result == 0) {
            throw new EmployeeNotFoundException(id);
        }
    }
}
