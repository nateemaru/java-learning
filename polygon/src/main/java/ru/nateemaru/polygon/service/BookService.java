package ru.nateemaru.polygon.service;

import ru.nateemaru.polygon.dto.request.CreateBookRequest;
import ru.nateemaru.polygon.dto.request.UpdateBookRequest;
import ru.nateemaru.polygon.dto.response.BookDto;

public interface BookService {
    BookDto create(CreateBookRequest request);
    BookDto getById(Long id);
    BookDto update(Long id, UpdateBookRequest request);
    void delete(Long id);
}
