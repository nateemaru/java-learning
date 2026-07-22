package ru.nateemaru.polygon.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.nateemaru.polygon.dto.request.CreateDepartmentRequest;
import ru.nateemaru.polygon.dto.request.UpdateDepartmentRequest;
import ru.nateemaru.polygon.dto.response.DepartmentDto;
import ru.nateemaru.polygon.service.department.DepartmentService;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
public class DepartmentControllerV1 {
    private final DepartmentService service;

    @PostMapping
    public ResponseEntity<DepartmentDto> create(@Valid @RequestBody CreateDepartmentRequest request) {
        DepartmentDto created = service.create(request.toCommand());

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
    public ResponseEntity<DepartmentDto> department(@PathVariable @Positive Long id) {
        DepartmentDto result = service.getById(id);
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<DepartmentDto> update(@PathVariable @Positive Long id,
                                                @Valid @RequestBody UpdateDepartmentRequest request) {
        DepartmentDto updated = service.updateById(id, request.toCommand());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
