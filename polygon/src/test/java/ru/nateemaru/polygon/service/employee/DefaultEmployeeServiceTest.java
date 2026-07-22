package ru.nateemaru.polygon.service.employee;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.nateemaru.polygon.dto.command.CreateEmployeeCommand;
import ru.nateemaru.polygon.dto.command.UpdateDepartmentCommand;
import ru.nateemaru.polygon.dto.command.UpdateEmployeeCommand;
import ru.nateemaru.polygon.dto.projection.EmployeeProjection;
import ru.nateemaru.polygon.dto.response.DepartmentDto;
import ru.nateemaru.polygon.dto.response.EmployeeDto;
import ru.nateemaru.polygon.dto.response.EmployeePreviewDto;
import ru.nateemaru.polygon.entity.Department;
import ru.nateemaru.polygon.entity.Employee;
import ru.nateemaru.polygon.entity.EmployeePosition;
import ru.nateemaru.polygon.exception.EmployeeNotFoundException;
import ru.nateemaru.polygon.mapping.EmployeeMapper;
import ru.nateemaru.polygon.repository.EmployeeRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DefaultEmployeeServiceTest {
    @Mock
    private EmployeeRepository repository;
    @Mock
    private EmployeeMapper mapper;

    @InjectMocks
    private DefaultEmployeeService service;

    @Test
    @DisplayName("Create Employee - Return Created Employee")
    void create_WithValidCommand_ReturnsDto() {
        Long id = 1L;
        String firstName = "Ivan";
        String lastName = "Ivanov";
        EmployeePosition position = EmployeePosition.Developer;
        Integer salary = 1000;
        Long departmentId = 1L;

        CreateEmployeeCommand command = new CreateEmployeeCommand(firstName, lastName, position, salary, departmentId);
        Employee afterSave = new Employee(id, firstName, lastName, position, salary, departmentId);
        EmployeeDto expected = new EmployeeDto(id, firstName, lastName, position, salary, departmentId);

        Mockito.when(repository.save(Mockito.any(Employee.class)))
                .thenReturn(afterSave);

        Mockito.when(mapper.toDto(afterSave))
                .thenReturn(expected);

        EmployeeDto actual = service.create(command);

        assertEquals(expected, actual);

        ArgumentCaptor<Employee> captor =
                ArgumentCaptor.forClass(Employee.class);

        Mockito.verify(repository).save(captor.capture());
        Mockito.verify(mapper).toDto(afterSave);

        Employee passedToRepository = captor.getValue();

        assertAll(
                () -> assertNull(passedToRepository.getId()),
                () -> assertEquals(firstName, passedToRepository.getFirstName()),
                () -> assertEquals(lastName, passedToRepository.getLastName()),
                () -> assertEquals(position, passedToRepository.getPosition()),
                () -> assertEquals(salary, passedToRepository.getSalary()),
                () -> assertEquals(departmentId, passedToRepository.getDepartmentId())
        );

        Mockito.verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    @DisplayName("Get Employee Preview - Success")
    void getById_WhenExists_ReturnsDto() {
        Long id = 1L;
        String fullName = "Firstname Lastname";
        EmployeePosition position = EmployeePosition.Developer;
        String departmentName = "Department name";

        EmployeeProjection projection = Mockito.mock(EmployeeProjection.class);

        EmployeePreviewDto expected = new EmployeePreviewDto(
                fullName,
                position,
                departmentName
        );

        Mockito.when(repository.findProjectionById(id))
                .thenReturn(Optional.of(projection));

        Mockito.when(mapper.toDto(projection))
                .thenReturn(expected);

        EmployeePreviewDto actual = service.getPreviewById(id);

        assertEquals(expected, actual);

        Mockito.verify(repository).findProjectionById(id);
        Mockito.verify(mapper).toDto(projection);
        Mockito.verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    @DisplayName("Get Employee Preview - Not Found")
    void getById_WhenDoesNotExists_ThrowsException() {
        Long id = 1L;

        Mockito.when(repository.findProjectionById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                EmployeeNotFoundException.class,
                () -> service.getPreviewById(id)
        );

        Mockito.verify(repository).findProjectionById(id);
        Mockito.verifyNoInteractions(mapper);
        Mockito.verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Update Employee - All Fields")
    void update_WithAllFieldsRequest_ChangesAllFields() {
        long id = 1L;
        String oldFirstName = "Old first name";
        String newFirstName = "New first name";
        String oldLastName = "Old last name";
        String newLastName = "New last name";
        EmployeePosition oldPosition = EmployeePosition.Developer;
        EmployeePosition newPosition = EmployeePosition.Cleaner;
        Integer oldSalary = 2000;
        Integer newSalary = 500;
        Long oldDepartmentId = 1L;
        Long newDepartmentId = 2L;
        Employee existing = new Employee(id, oldFirstName, oldLastName, oldPosition, oldSalary, oldDepartmentId);
        Employee saved = new Employee(id, oldFirstName, oldLastName, oldPosition, oldSalary, oldDepartmentId);
        UpdateEmployeeCommand command = new UpdateEmployeeCommand(newFirstName, newLastName, newPosition, newSalary, newDepartmentId);
        EmployeeDto expected = new EmployeeDto(id, newFirstName, newLastName, newPosition, newSalary, newDepartmentId);

        Mockito.when(repository.findById(id))
                .thenReturn(Optional.of(existing));

        Mockito.when(repository.save(Mockito.any(Employee.class)))
                .thenReturn(saved);

        Mockito.when(mapper.toDto(saved))
                .thenReturn(expected);

        EmployeeDto actual = service.updateById(id, command);

        assertEquals(expected, actual);

        ArgumentCaptor<Employee> captor =
                ArgumentCaptor.forClass(Employee.class);

        Mockito.verify(repository).findById(id);
        Mockito.verify(repository).save(captor.capture());
        Mockito.verify(mapper).toDto(saved);

        Employee updated = captor.getValue();

        assertSame(existing, updated);
        assertEquals(newFirstName, updated.getFirstName());
        assertEquals(newLastName, updated.getLastName());
        assertEquals(newPosition, updated.getPosition());
        assertEquals(newSalary, updated.getSalary());
        assertEquals(newDepartmentId, updated.getDepartmentId());

        Mockito.verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    @DisplayName("Update Employee - Only First Name")
    void update_WithOnlyFirstNameFieldRequest_ChangesFirstName() {
        long id = 1L;
        String oldFirstName = "Old first name";
        String newFirstName = "New first name";
        String oldLastName = "Old last name";
        EmployeePosition oldPosition = EmployeePosition.Developer;
        Integer oldSalary = 2000;
        Long oldDepartmentId = 1L;
        Employee existing = new Employee(id, oldFirstName, oldLastName, oldPosition, oldSalary, oldDepartmentId);
        Employee saved = new Employee(id, oldFirstName, oldLastName, oldPosition, oldSalary, oldDepartmentId);
        UpdateEmployeeCommand command = new UpdateEmployeeCommand(newFirstName, oldLastName, oldPosition, oldSalary, oldDepartmentId);
        EmployeeDto expected = new EmployeeDto(id, newFirstName, oldLastName, oldPosition, oldSalary, oldDepartmentId);

        Mockito.when(repository.findById(id))
                .thenReturn(Optional.of(existing));

        Mockito.when(repository.save(Mockito.any(Employee.class)))
                .thenReturn(saved);

        Mockito.when(mapper.toDto(saved))
                .thenReturn(expected);

        EmployeeDto actual = service.updateById(id, command);

        assertEquals(expected, actual);

        ArgumentCaptor<Employee> captor =
                ArgumentCaptor.forClass(Employee.class);

        Mockito.verify(repository).findById(id);
        Mockito.verify(repository).save(captor.capture());
        Mockito.verify(mapper).toDto(saved);

        Employee updated = captor.getValue();

        assertSame(existing, updated);
        assertEquals(newFirstName, updated.getFirstName());
        assertEquals(oldLastName, updated.getLastName());
        assertEquals(oldPosition, updated.getPosition());
        assertEquals(oldSalary, updated.getSalary());
        assertEquals(oldDepartmentId, updated.getDepartmentId());

        Mockito.verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    @DisplayName("Update Employee - Only Last Name")
    void update_WithOnlyLastNameFieldRequest_ChangesLastName() {
        long id = 1L;
        String oldFirstName = "Old first name";
        String oldLastName = "Old last name";
        String newLastName = "New last name";
        EmployeePosition oldPosition = EmployeePosition.Developer;
        Integer oldSalary = 2000;
        Long oldDepartmentId = 1L;
        Employee existing = new Employee(id, oldFirstName, oldLastName, oldPosition, oldSalary, oldDepartmentId);
        Employee saved = new Employee(id, oldFirstName, oldLastName, oldPosition, oldSalary, oldDepartmentId);
        UpdateEmployeeCommand command = new UpdateEmployeeCommand(oldFirstName, newLastName, oldPosition, oldSalary, oldDepartmentId);
        EmployeeDto expected = new EmployeeDto(id, oldFirstName, newLastName, oldPosition, oldSalary, oldDepartmentId);

        Mockito.when(repository.findById(id))
                .thenReturn(Optional.of(existing));

        Mockito.when(repository.save(Mockito.any(Employee.class)))
                .thenReturn(saved);

        Mockito.when(mapper.toDto(saved))
                .thenReturn(expected);

        EmployeeDto actual = service.updateById(id, command);

        assertEquals(expected, actual);

        ArgumentCaptor<Employee> captor =
                ArgumentCaptor.forClass(Employee.class);

        Mockito.verify(repository).findById(id);
        Mockito.verify(repository).save(captor.capture());
        Mockito.verify(mapper).toDto(saved);

        Employee updated = captor.getValue();

        assertSame(existing, updated);
        assertEquals(oldFirstName, updated.getFirstName());
        assertEquals(newLastName, updated.getLastName());
        assertEquals(oldPosition, updated.getPosition());
        assertEquals(oldSalary, updated.getSalary());
        assertEquals(oldDepartmentId, updated.getDepartmentId());

        Mockito.verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    @DisplayName("Update Employee - Only Position")
    void update_WithOnlyPositionFieldRequest_ChangesPosition() {
        long id = 1L;
        String oldFirstName = "Old first name";
        String oldLastName = "Old last name";
        EmployeePosition oldPosition = EmployeePosition.Developer;
        EmployeePosition newPosition = EmployeePosition.Cleaner;
        Integer oldSalary = 2000;
        Long oldDepartmentId = 1L;
        Employee existing = new Employee(id, oldFirstName, oldLastName, oldPosition, oldSalary, oldDepartmentId);
        Employee saved = new Employee(id, oldFirstName, oldLastName, oldPosition, oldSalary, oldDepartmentId);
        UpdateEmployeeCommand command = new UpdateEmployeeCommand(oldFirstName, oldLastName, newPosition, oldSalary, oldDepartmentId);
        EmployeeDto expected = new EmployeeDto(id, oldFirstName, oldLastName, newPosition, oldSalary, oldDepartmentId);

        Mockito.when(repository.findById(id))
                .thenReturn(Optional.of(existing));

        Mockito.when(repository.save(Mockito.any(Employee.class)))
                .thenReturn(saved);

        Mockito.when(mapper.toDto(saved))
                .thenReturn(expected);

        EmployeeDto actual = service.updateById(id, command);

        assertEquals(expected, actual);

        ArgumentCaptor<Employee> captor =
                ArgumentCaptor.forClass(Employee.class);

        Mockito.verify(repository).findById(id);
        Mockito.verify(repository).save(captor.capture());
        Mockito.verify(mapper).toDto(saved);

        Employee updated = captor.getValue();

        assertSame(existing, updated);
        assertEquals(oldFirstName, updated.getFirstName());
        assertEquals(oldLastName, updated.getLastName());
        assertEquals(newPosition, updated.getPosition());
        assertEquals(oldSalary, updated.getSalary());
        assertEquals(oldDepartmentId, updated.getDepartmentId());

        Mockito.verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    @DisplayName("Update Employee - Only Salary")
    void update_WithOnlySalaryFieldRequest_ChangesSalary() {
        long id = 1L;
        String oldFirstName = "Old first name";
        String oldLastName = "Old last name";
        EmployeePosition oldPosition = EmployeePosition.Developer;
        Integer oldSalary = 2000;
        Integer newSalary = 500;
        Long oldDepartmentId = 1L;
        Employee existing = new Employee(id, oldFirstName, oldLastName, oldPosition, oldSalary, oldDepartmentId);
        Employee saved = new Employee(id, oldFirstName, oldLastName, oldPosition, oldSalary, oldDepartmentId);
        UpdateEmployeeCommand command = new UpdateEmployeeCommand(oldFirstName, oldLastName, oldPosition, newSalary, oldDepartmentId);
        EmployeeDto expected = new EmployeeDto(id, oldFirstName, oldLastName, oldPosition, newSalary, oldDepartmentId);

        Mockito.when(repository.findById(id))
                .thenReturn(Optional.of(existing));

        Mockito.when(repository.save(Mockito.any(Employee.class)))
                .thenReturn(saved);

        Mockito.when(mapper.toDto(saved))
                .thenReturn(expected);

        EmployeeDto actual = service.updateById(id, command);

        assertEquals(expected, actual);

        ArgumentCaptor<Employee> captor =
                ArgumentCaptor.forClass(Employee.class);

        Mockito.verify(repository).findById(id);
        Mockito.verify(repository).save(captor.capture());
        Mockito.verify(mapper).toDto(saved);

        Employee updated = captor.getValue();

        assertSame(existing, updated);
        assertEquals(oldFirstName, updated.getFirstName());
        assertEquals(oldLastName, updated.getLastName());
        assertEquals(oldPosition, updated.getPosition());
        assertEquals(newSalary, updated.getSalary());
        assertEquals(oldDepartmentId, updated.getDepartmentId());

        Mockito.verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    @DisplayName("Update Employee - Only Department Id")
    void update_WithOnlyDepartmentIdFieldRequest_ChangesDepartmentId() {
        long id = 1L;
        String oldFirstName = "Old first name";
        String oldLastName = "Old last name";
        EmployeePosition oldPosition = EmployeePosition.Developer;
        Integer oldSalary = 2000;
        Long oldDepartmentId = 1L;
        Long newDepartmentId = 2L;
        Employee existing = new Employee(id, oldFirstName, oldLastName, oldPosition, oldSalary, oldDepartmentId);
        Employee saved = new Employee(id, oldFirstName, oldLastName, oldPosition, oldSalary, oldDepartmentId);
        UpdateEmployeeCommand command = new UpdateEmployeeCommand(oldFirstName, oldLastName, oldPosition, oldSalary, newDepartmentId);
        EmployeeDto expected = new EmployeeDto(id, oldFirstName, oldLastName, oldPosition, oldSalary, newDepartmentId);

        Mockito.when(repository.findById(id))
                .thenReturn(Optional.of(existing));

        Mockito.when(repository.save(Mockito.any(Employee.class)))
                .thenReturn(saved);

        Mockito.when(mapper.toDto(saved))
                .thenReturn(expected);

        EmployeeDto actual = service.updateById(id, command);

        assertEquals(expected, actual);

        ArgumentCaptor<Employee> captor =
                ArgumentCaptor.forClass(Employee.class);

        Mockito.verify(repository).findById(id);
        Mockito.verify(repository).save(captor.capture());
        Mockito.verify(mapper).toDto(saved);

        Employee updated = captor.getValue();

        assertSame(existing, updated);
        assertEquals(oldFirstName, updated.getFirstName());
        assertEquals(oldLastName, updated.getLastName());
        assertEquals(oldPosition, updated.getPosition());
        assertEquals(oldSalary, updated.getSalary());
        assertEquals(newDepartmentId, updated.getDepartmentId());

        Mockito.verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    @DisplayName("Delete Employee - Success")
    void delete_WhenExists_ReturnsDto() {
        Long id = 1L;

        Mockito.when(repository.deleteAndCountById(id))
                .thenReturn(1);

        assertDoesNotThrow(() -> service.deleteById(id));

        Mockito.verify(repository).deleteAndCountById(id);
        Mockito.verifyNoInteractions(mapper);
    }

    @Test
    @DisplayName("Delete Employee - Not Found")
    void delete_WhenDoesNotExists_ThrowsException() {
        Long id = 1L;

        Mockito.when(repository.deleteAndCountById(id))
                .thenReturn(0);

        assertThrows(
                EmployeeNotFoundException.class,
                () -> service.deleteById(id)
        );

        Mockito.verify(repository).deleteAndCountById(id);
        Mockito.verifyNoInteractions(mapper);
    }
}