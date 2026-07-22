package ru.nateemaru.polygon.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.nateemaru.polygon.dto.request.CreateEmployeeRequest;
import ru.nateemaru.polygon.dto.request.UpdateEmployeeRequest;
import ru.nateemaru.polygon.dto.response.EmployeeDto;
import ru.nateemaru.polygon.dto.response.EmployeePreviewDto;
import ru.nateemaru.polygon.service.employee.EmployeeService;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class EmployeeControllerV1 {
    private final EmployeeService service;

    @PostMapping
    public ResponseEntity<EmployeeDto> create(@Valid @RequestBody CreateEmployeeRequest request) {
        EmployeeDto created = service.create(request.toCommand());

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeePreviewDto> employeePreview(@PathVariable @Positive Long id) {
        EmployeePreviewDto result = service.getPreviewById(id);
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<EmployeeDto> update(@PathVariable @Positive Long id,
                                                     @Valid @RequestBody UpdateEmployeeRequest request) {
        EmployeeDto updated = service.updateById(id, request.toCommand());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
