package ru.nateemaru.polygon.controller;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.nateemaru.polygon.dto.request.CreateUserRequest;
import ru.nateemaru.polygon.dto.request.UpdateUserSummaryRequest;
import ru.nateemaru.polygon.dto.response.UserDto;
import ru.nateemaru.polygon.dto.response.UserViews;
import ru.nateemaru.polygon.dto.response.UsersPageDto;
import ru.nateemaru.polygon.service.user.UserService;
import ru.nateemaru.polygon.service.user.UserSortField;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/user/")
@RequiredArgsConstructor
public class UserControllerV1 {
    private final UserService service;

    @PostMapping("/create")
    @JsonView(UserViews.Summary.class)
    public ResponseEntity<UserDto> create(@Valid @RequestBody CreateUserRequest request) {
        UserDto created = service.create(request.name(), request.email());
        return ResponseEntity
                .created(URI.create("/api/v1/user/" + created.id()))
                .body(created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @JsonView(UserViews.Summary.class)
    public ResponseEntity<UserDto> update(@PathVariable @Positive Long id,
                                          @Valid @RequestBody UpdateUserSummaryRequest request) {
        UserDto updated = service.update(id, request.name(), request.email());
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    @JsonView(UserViews.Details.class)
    public ResponseEntity<UserDto> userWithOrders(@PathVariable @Positive Long id) {
        UserDto user = service.getWithOrders(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/search")
    @JsonView(UserViews.Summary.class)
    public ResponseEntity<UsersPageDto> search(@RequestParam(name = "page", defaultValue = "0") @PositiveOrZero Integer page,
                                               @RequestParam(name = "size", defaultValue = "20") @Positive Integer size,
                                               @RequestParam(name = "sortBy", defaultValue = "ID") UserSortField sortBy,
                                               @RequestParam(name = "direction", defaultValue = "ASC") Sort.Direction direction) {
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

        UsersPageDto batch = service.getPage(pageable);
        return ResponseEntity.ok(batch);
    }
}
