package ru.nateemaru.polygon.service.department;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.nateemaru.polygon.dto.command.CreateDepartmentCommand;
import ru.nateemaru.polygon.dto.command.UpdateDepartmentCommand;
import ru.nateemaru.polygon.dto.response.DepartmentDto;
import ru.nateemaru.polygon.entity.Department;
import ru.nateemaru.polygon.exception.DepartmentNotFoundException;
import ru.nateemaru.polygon.mapping.DepartmentMapper;
import ru.nateemaru.polygon.repository.DepartmentRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DefaultDepartmentServiceTest {
    @Mock
    private DepartmentRepository repository;
    @Mock
    private DepartmentMapper mapper;

    @InjectMocks
    private DefaultDepartmentService service;

    @Test
    @DisplayName("Create Department - Return Created Department")
    void create_WithValidCommand_ReturnsDto() {
        Long id = 1L;
        String name = "name";

        CreateDepartmentCommand command = new CreateDepartmentCommand(name);
        Department afterSave = new Department(id, name);
        DepartmentDto expected = new DepartmentDto(id, name);

        Mockito.when(repository.save(Mockito.any(Department.class)))
                .thenReturn(afterSave);

        Mockito.when(mapper.toDto(afterSave))
                .thenReturn(expected);

        DepartmentDto actual = service.create(command);

        assertEquals(expected, actual);

        ArgumentCaptor<Department> captor =
                ArgumentCaptor.forClass(Department.class);

        Mockito.verify(repository).save(captor.capture());
        Mockito.verify(mapper).toDto(afterSave);

        Department passedToRepository = captor.getValue();

        assertAll(
                () -> assertNull(passedToRepository.getId()),
                () -> assertEquals(name, passedToRepository.getName())
        );

        Mockito.verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    @DisplayName("Get Department - Success")
    void getById_WhenDepartmentExists_ReturnsDto() {
        Long id = 1L;
        String name = "name";

        var saved = new Department(id, name);
        var expected = new DepartmentDto(id, name);

        Mockito.when(repository.findById(id))
                .thenReturn(Optional.of(saved));

        Mockito.when(mapper.toDto(saved))
                .thenReturn(expected);

        DepartmentDto actual = service.getById(id);

        assertEquals(expected, actual);

        Mockito.verify(repository).findById(id);
        Mockito.verify(mapper).toDto(saved);
    }

    @Test
    @DisplayName("Get Department - Not Found")
    void getById_WhenDepartmentDoesNotExists_ThrowsException() {
        Long id = 1L;

        Mockito.when(repository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                DepartmentNotFoundException.class,
                () -> service.getById(id)
        );

        Mockito.verify(repository).findById(id);
        Mockito.verifyNoInteractions(mapper);
        Mockito.verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Update Department - All Fields")
    void update_WithAllFieldsRequest_ChangesAllFields() {
        long id = 1L;
        String oldName = "Old name";
        String newName = "New name";
        Department existing = new Department(id, oldName);
        Department saved = new Department(id, newName);
        UpdateDepartmentCommand command = new UpdateDepartmentCommand(newName);
        DepartmentDto expected = new DepartmentDto(id, newName);

        Mockito.when(repository.findById(id))
                .thenReturn(Optional.of(existing));

        Mockito.when(repository.save(Mockito.any(Department.class)))
                .thenReturn(saved);

        Mockito.when(mapper.toDto(saved))
                .thenReturn(expected);

        DepartmentDto actual = service.updateById(id, command);

        assertEquals(expected, actual);

        ArgumentCaptor<Department> captor =
                ArgumentCaptor.forClass(Department.class);

        Mockito.verify(repository).findById(id);
        Mockito.verify(repository).save(captor.capture());
        Mockito.verify(mapper).toDto(saved);

        Department updated = captor.getValue();

        assertSame(existing, updated);
        assertEquals(newName, updated.getName());

        Mockito.verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    @DisplayName("Delete Department - Success")
    void delete_WhenDepartmentExists_ReturnsDto() {
        Long id = 1L;

        Mockito.when(repository.deleteAndCountById(id))
                .thenReturn(1);

        assertDoesNotThrow(() -> service.deleteById(id));

        Mockito.verify(repository).deleteAndCountById(id);
        Mockito.verifyNoInteractions(mapper);
    }

    @Test
    @DisplayName("Delete Department - Not Found")
    void delete_WhenDepartmentDoesNotExists_ThrowsException() {
        Long id = 1L;

        Mockito.when(repository.deleteAndCountById(id))
                .thenReturn(0);

        assertThrows(
                DepartmentNotFoundException.class,
                () -> service.deleteById(id)
        );

        Mockito.verify(repository).deleteAndCountById(id);
        Mockito.verifyNoInteractions(mapper);
    }
}