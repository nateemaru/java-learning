package ru.nateemaru.polygon.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.nateemaru.polygon.dto.request.CreateBookRequest;
import ru.nateemaru.polygon.dto.request.UpdateBookRequest;
import ru.nateemaru.polygon.dto.response.BookDto;
import ru.nateemaru.polygon.entity.Book;
import ru.nateemaru.polygon.exception.BookNotFoundException;
import ru.nateemaru.polygon.mapping.BookMapper;
import ru.nateemaru.polygon.repository.BookRepository;

@Service
@RequiredArgsConstructor
public class DefaultBookService implements BookService {
    private final BookRepository repository;
    private final BookMapper mapper;

    @Override
    public BookDto create(CreateBookRequest request) {
        Book createdBook = repository.save(mapper.toEntity(request));
        return mapper.toDto(createdBook);
    }

    @Override
    public BookDto getById(Long id) {
        Book book = repository.findById(id).orElseThrow(() -> new BookNotFoundException(id));
        return mapper.toDto(book);
    }

    @Override
    public BookDto update(Long id, UpdateBookRequest request) {
        Book book = repository.findById(id).orElseThrow(() -> new BookNotFoundException(id));

        if (request.title() != null) {
            book.setTitle(request.title());
        }

        if (request.author() != null) {
            book.setAuthor(request.author());
        }

        if (request.publicationYear() != null) {
            book.setPublicationYear(request.publicationYear());
        }

        return mapper.toDto(repository.update(book).orElseThrow(() -> new BookNotFoundException(id)));
    }

    @Override
    public void delete(Long id) {
        int result = repository.delete(id);

        if (result == 0) {
            throw new BookNotFoundException(id);
        }
    }
}
