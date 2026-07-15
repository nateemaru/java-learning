package ru.nateemaru.polygon.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.nateemaru.polygon.dto.request.UpdateUserRequest;
import ru.nateemaru.polygon.dto.response.UserDto;
import ru.nateemaru.polygon.dto.response.UsersDto;
import ru.nateemaru.polygon.service.user.UserService;
import ru.nateemaru.polygon.service.user.UserSortField;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/user/")
@Validated
@RequiredArgsConstructor
public class UserControllerV1 {
    private final UserService service;

    @PostMapping("/create")
    public ResponseEntity<UserDto> create() {
        UserDto created = service.create();
        return ResponseEntity.created(URI.create("/api/v1/user/" + created.id())).body(created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> update(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
        UserDto updated = service.update(request.user());
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> userWithOrders(@PathVariable @Positive Long id) {
        UserDto user = service.getWithOrders(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/search")
    public ResponseEntity<UsersDto> search(@RequestParam(defaultValue = "0") @PositiveOrZero Integer page,
                                           @RequestParam(defaultValue = "20") @Positive Integer size,
                                           @RequestParam(defaultValue = "ID") UserSortField sortBy,
                                           @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        String property = switch (sortBy) {
            case ID -> "id";
            case USERNAME -> "username";
            case EMAIL -> "email";
            case CREATED_AT -> "createdAt";
        };

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(direction, property)
        );

        UsersDto batch = service.getPage(pageable);
        return ResponseEntity.ok(batch);
    }
}
