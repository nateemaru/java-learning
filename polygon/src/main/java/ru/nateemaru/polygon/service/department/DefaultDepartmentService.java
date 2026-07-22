package ru.nateemaru.polygon.service.department;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import ru.nateemaru.polygon.dto.command.CreateDepartmentCommand;
import ru.nateemaru.polygon.dto.command.UpdateDepartmentCommand;
import ru.nateemaru.polygon.dto.response.DepartmentDto;
import ru.nateemaru.polygon.entity.Department;
import ru.nateemaru.polygon.exception.DepartmentAlreadyExistsException;
import ru.nateemaru.polygon.exception.DepartmentNotFoundException;
import ru.nateemaru.polygon.mapping.DepartmentMapper;
import ru.nateemaru.polygon.repository.DepartmentRepository;

@Service
@RequiredArgsConstructor
public class DefaultDepartmentService implements DepartmentService {
    private final DepartmentRepository repository;
    private final DepartmentMapper mapper;

    @Override
    public DepartmentDto create(CreateDepartmentCommand command) {
        Department department = new Department(
                null,
                command.name()
        );

        try {
            Department saved = repository.save(department);
            return mapper.toDto(saved);
        } catch (DuplicateKeyException e) {
            throw new DepartmentAlreadyExistsException(command.name());
        }
    }

    @Override
    public DepartmentDto getById(Long id) {
        Department department = repository.findById(id).orElseThrow(() -> new DepartmentNotFoundException(id));
        return mapper.toDto(department);
    }

    @Override
    public DepartmentDto updateById(Long id, UpdateDepartmentCommand command) {
        Department department = repository.findById(id).orElseThrow(() -> new DepartmentNotFoundException(id));

        if (command.name() != null) {
            department.setName(command.name());
        }

        Department saved = repository.save(department);
        return mapper.toDto(saved);
    }

    @Override
    public void deleteById(Long id) {
        int affectedRows = repository.deleteAndCountById(id);

        if (affectedRows == 0) {
            throw new DepartmentNotFoundException(id);
        }
    }
}
