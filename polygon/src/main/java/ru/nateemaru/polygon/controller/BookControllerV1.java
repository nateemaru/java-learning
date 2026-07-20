package ru.nateemaru.polygon.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.nateemaru.polygon.dto.request.CreateBookRequest;
import ru.nateemaru.polygon.dto.request.UpdateBookRequest;
import ru.nateemaru.polygon.dto.response.BookDto;
import ru.nateemaru.polygon.service.BookService;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/book")
@RequiredArgsConstructor
public class BookControllerV1 {
    private final BookService service;

    @PostMapping("/create")
    public ResponseEntity<BookDto> create(@Valid @RequestBody CreateBookRequest request) {
        BookDto created = service.create(request);
        return ResponseEntity
                .created(URI.create("/api/v1/book/" + created.id()))
                .body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDto> book(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BookDto> update(@PathVariable @Positive Long id,
                                          @Valid @RequestBody UpdateBookRequest request) {
        BookDto updated = service.update(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
