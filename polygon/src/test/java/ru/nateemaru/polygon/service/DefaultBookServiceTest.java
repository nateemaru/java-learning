package ru.nateemaru.polygon.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.nateemaru.polygon.dto.request.CreateBookRequest;
import ru.nateemaru.polygon.dto.request.UpdateBookRequest;
import ru.nateemaru.polygon.dto.response.BookDto;
import ru.nateemaru.polygon.entity.Book;
import ru.nateemaru.polygon.exception.BookNotFoundException;
import ru.nateemaru.polygon.mapping.BookMapper;
import ru.nateemaru.polygon.repository.BookRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DefaultBookServiceTest {
    @Mock
    private BookRepository repository;
    @Mock
    private BookMapper mapper;

    @InjectMocks
    private DefaultBookService service;

    @Test
    @DisplayName("Create Book - Return Created Book")
    void create_withValidRequest_ReturnsBookDto() {
        Long id = 1L;
        String title = "Title";
        String author = "Author";
        Integer publicationYear = 1900;

        var request = new CreateBookRequest(title, author, publicationYear);
        var unsavedBook = new Book(null, title, author, publicationYear);
        var savedBook = new Book(id, title, author, publicationYear);
        var expected = new BookDto(id, title, author, publicationYear);

        Mockito.when(mapper.toEntity(request))
                .thenReturn(unsavedBook);

        Mockito.when(repository.save(unsavedBook))
                .thenReturn(savedBook);

        Mockito.when(mapper.toDto(savedBook))
                .thenReturn(expected);

        BookDto actual = service.create(request);

        assertEquals(expected, actual);

        Mockito.verify(mapper).toEntity(request);
        Mockito.verify(repository).save(unsavedBook);
        Mockito.verify(mapper).toDto(savedBook);
        Mockito.verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    @DisplayName("Get Book - Success")
    void getById_WhenBookExists_ReturnsBookDto() {
        Long id = 1L;
        String title = "Title";
        String author = "Author";
        Integer publicationYear = 1900;

        var savedBook = new Book(id, title, author, publicationYear);
        var expected = new BookDto(id, title, author, publicationYear);

        Mockito.when(repository.findById(id))
                .thenReturn(Optional.of(savedBook));

        Mockito.when(mapper.toDto(savedBook))
                .thenReturn(expected);

        BookDto actual = service.getById(id);

        assertEquals(expected, actual);

        Mockito.verify(repository).findById(id);
        Mockito.verify(mapper).toDto(savedBook);
    }

    @Test
    @DisplayName("Get Book - Not Found")
    void getById_WhenBookDoesNotExists_ThrowsException() {
        Long id = 1L;

        Mockito.when(repository.findById(id))
                .thenThrow(new BookNotFoundException(id));

        assertThrows(
                BookNotFoundException.class,
                () -> service.getById(id)
        );

        Mockito.verify(repository).findById(id);
        Mockito.verifyNoInteractions(mapper);
    }

    @Test
    @DisplayName("Update Book - All Fields")
    void update_WithAllFieldsRequest_ChangesAllFields() {
        long id = 1L;
        String newTitle = "New title";
        String oldTitle = "Old title";
        String newAuthor = "New author";
        String oldAuthor = "Old author";
        Integer newPublicationYear = 2000;
        Integer oldPublicationYear = 1900;
        Book book = new Book(id, oldTitle, oldAuthor, oldPublicationYear);
        var request = new UpdateBookRequest(newTitle, newAuthor, newPublicationYear);
        var expected = new BookDto(id, newTitle, newAuthor, newPublicationYear);

        Mockito.when(repository.findById(id))
                .thenReturn(Optional.of(book));

        Mockito.when(repository.update(book))
                .thenReturn(Optional.of(book));

        Mockito.when(mapper.toDto(book))
                .thenReturn(expected);

        BookDto actual = service.update(id, request);

        assertEquals(expected, actual);
        assertEquals(newTitle, book.getTitle());
        assertEquals(newAuthor, book.getAuthor());
        assertEquals(newPublicationYear, book.getPublicationYear());

        Mockito.verify(repository).findById(id);
        Mockito.verify(repository).update(book);
        Mockito.verify(mapper).toDto(book);
    }

    @Test
    @DisplayName("Update Book - Only Title")
    void update_WithOnlyNewTitleRequest_ChangesOnlyTitle() {
        long id = 1L;
        String newTitle = "New title";
        String oldTitle = "Old title";
        String oldAuthor = "Old author";
        Integer oldPublicationYear = 1900;
        Book book = new Book(id, oldTitle, oldAuthor, oldPublicationYear);
        var request = new UpdateBookRequest(newTitle, null, null);
        var expected = new BookDto(id, newTitle, oldAuthor, oldPublicationYear);

        Mockito.when(repository.findById(id))
                .thenReturn(Optional.of(book));

        Mockito.when(repository.update(book))
                .thenReturn(Optional.of(book));

        Mockito.when(mapper.toDto(book))
                .thenReturn(expected);

        BookDto actual = service.update(id, request);

        assertEquals(expected, actual);
        assertEquals(newTitle, book.getTitle());
        assertEquals(oldAuthor, book.getAuthor());
        assertEquals(oldPublicationYear, book.getPublicationYear());

        Mockito.verify(repository).findById(id);
        Mockito.verify(repository).update(book);
        Mockito.verify(mapper).toDto(book);
    }

    @Test
    @DisplayName("Update Book - Only Author")
    void update_WithOnlyNewAuthorRequest_ChangesOnlyAuthor() {
        long id = 1L;
        String oldTitle = "Old title";
        String newAuthor = "New author";
        String oldAuthor = "Old author";
        Integer oldPublicationYear = 1900;
        Book book = new Book(id, oldTitle, oldAuthor, oldPublicationYear);
        var request = new UpdateBookRequest(null, newAuthor, null);
        var expected = new BookDto(id, oldAuthor, newAuthor, oldPublicationYear);

        Mockito.when(repository.findById(id))
                .thenReturn(Optional.of(book));

        Mockito.when(repository.update(book))
                .thenReturn(Optional.of(book));

        Mockito.when(mapper.toDto(book))
                .thenReturn(expected);

        BookDto actual = service.update(id, request);

        assertEquals(expected, actual);
        assertEquals(oldTitle, book.getTitle());
        assertEquals(newAuthor, book.getAuthor());
        assertEquals(oldPublicationYear, book.getPublicationYear());

        Mockito.verify(repository).findById(id);
        Mockito.verify(repository).update(book);
        Mockito.verify(mapper).toDto(book);
    }

    @Test
    @DisplayName("Update Book - Only Publication Year")
    void update_WithOnlyNewPublicationYearRequest_ChangesOnlyPublicationYear() {
        long id = 1L;
        String oldTitle = "Old title";
        String oldAuthor = "Old author";
        Integer newPublicationYear = 2000;
        Integer oldPublicationYear = 1900;
        Book book = new Book(id, oldTitle, oldAuthor, oldPublicationYear);
        var request = new UpdateBookRequest(null, null, newPublicationYear);
        var expected = new BookDto(id, oldAuthor, oldAuthor, newPublicationYear);

        Mockito.when(repository.findById(id))
                .thenReturn(Optional.of(book));

        Mockito.when(repository.update(book))
                .thenReturn(Optional.of(book));

        Mockito.when(mapper.toDto(book))
                .thenReturn(expected);

        BookDto actual = service.update(id, request);

        assertEquals(expected, actual);
        assertEquals(oldTitle, book.getTitle());
        assertEquals(oldAuthor, book.getAuthor());
        assertEquals(newPublicationYear, book.getPublicationYear());

        Mockito.verify(repository).findById(id);
        Mockito.verify(repository).update(book);
        Mockito.verify(mapper).toDto(book);
    }

    @Test
    @DisplayName("Delete Book - Success")
    void delete_WhenBookExists_ReturnsBookDto() {
        Long id = 1L;

        Mockito.when(repository.delete(id))
                .thenReturn(1);

        assertDoesNotThrow(() -> service.delete(id));

        Mockito.verify(repository).delete(id);
        Mockito.verifyNoInteractions(mapper);
    }

    @Test
    @DisplayName("Delete Book - Not Found")
    void delete_WhenBookDoesNotExists_ThrowsException() {
        Long id = 1L;

        Mockito.when(repository.delete(id))
                .thenReturn(0);

        assertThrows(
                BookNotFoundException.class,
                () -> service.delete(id)
        );

        Mockito.verify(repository).delete(id);
        Mockito.verifyNoInteractions(mapper);
    }
}