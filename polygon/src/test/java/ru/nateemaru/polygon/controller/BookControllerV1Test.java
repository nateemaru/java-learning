package ru.nateemaru.polygon.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.nateemaru.polygon.dto.request.CreateBookRequest;
import ru.nateemaru.polygon.dto.request.UpdateBookRequest;
import ru.nateemaru.polygon.dto.response.BookDto;
import ru.nateemaru.polygon.exception.BookNotFoundException;
import ru.nateemaru.polygon.service.BookService;
import tools.jackson.databind.ObjectMapper;

import java.util.stream.Stream;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("BookControllerV1 tests")
@WebMvcTest(controllers = BookControllerV1.class)
class BookControllerV1Test {
    @MockitoBean
    private BookService bookService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Create Book - Success")
    void create_WithValidRequest_Returns201AndValidBookDto() throws Exception {
        Long id = 1L;
        String title = "Title";
        String author = "Author";
        Integer publicationYear = 1900;
        BookDto response = new BookDto(id, title, author, publicationYear);
        CreateBookRequest request = new CreateBookRequest(title, author, publicationYear);

        Mockito.when(bookService.create(new CreateBookRequest(title, author, publicationYear)))
                .thenReturn(response);

        var requestBuilder = MockMvcRequestBuilders.post("/api/v1/book/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isCreated(),
                        header().string("Location", "/api/v1/book/" + id),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        jsonPath("$.id").value(id),
                        jsonPath("$.title").value(title),
                        jsonPath("$.author").value(author),
                        jsonPath("$.publicationYear").value(publicationYear)
                );

        Mockito.verify(bookService).create(request);
    }

    @ParameterizedTest
    @MethodSource("invalidCreateRequests")
    @DisplayName("Create Book - Invalid Request")
    void create_WithInvalidRequest_Returns400(CreateBookRequest request) throws Exception {
        var requestBuilder = MockMvcRequestBuilders.post("/api/v1/book/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                );

        Mockito.verifyNoInteractions(this.bookService);
    }

    private static Stream<Arguments> invalidCreateRequests() {
        return Stream.of(
                Arguments.of(new CreateBookRequest(null, "Author", 1900)),
                Arguments.of(new CreateBookRequest("Title", null, 1900)),
                Arguments.of(new CreateBookRequest("Title", "Author", null)),
                Arguments.of(new CreateBookRequest("", "Author", 1900)),
                Arguments.of(new CreateBookRequest("Title", "", 1900)),
                Arguments.of(new CreateBookRequest("   ", "Author", -1900)),
                Arguments.of(new CreateBookRequest("Title", "   ", 1900)),
                Arguments.of(new CreateBookRequest("Title", "Author", 0))
        );
    }

    @Test
    @DisplayName("Create Book - Missing Required Fields")
    void create_WithMissingFields_Returns400() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.post("/api/v1/book/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}");

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                );
        Mockito.verifyNoInteractions(this.bookService);
    }

    @Test
    @DisplayName("Create Book - Bad JSON")
    void create_WithBadJSON_Returns400() throws Exception {
        String malformedJson = "{" + "bad json" + "}";
        var requestBuilder = MockMvcRequestBuilders.post("/api/v1/book/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(malformedJson);

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                );
        Mockito.verifyNoInteractions(this.bookService);
    }

    @Test
    @DisplayName("Get Book - Success")
    void book_withValidId_Returns200AndValidDto() throws Exception {
        Long id = 1L;
        String title = "Title";
        String author = "Author";
        Integer publicationYear = 1900;
        BookDto result = new BookDto(id, title, author, publicationYear);

        var requestBuilder = MockMvcRequestBuilders.get("/api/v1/book/{id}", id)
                .accept(MediaType.APPLICATION_JSON);

        Mockito.when(bookService.getById(id)).thenReturn(result);

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        jsonPath("$.id").value(id),
                        jsonPath("$.title").value(title),
                        jsonPath("$.author").value(author),
                        jsonPath("$.publicationYear").value(publicationYear)
                );

        Mockito.verify(bookService).getById(id);
    }

    @Test
    @DisplayName("Get Book - Not Found")
    void book_WithNonExistingId_Returns404() throws Exception {
        Long id = 1L;

        Mockito.when(bookService.getById(id)).thenThrow(new BookNotFoundException(id));

        var requestBuilder = MockMvcRequestBuilders.get("/api/v1/book/{id}", id)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isNotFound(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                );

        Mockito.verify(bookService).getById(id);
    }

    @Test
    @DisplayName("Get Book - Not Positive Id")
    void book_withNotPositiveId_Returns400() throws Exception {
        Long id = -1L;

        var requestBuilder = MockMvcRequestBuilders.get("/api/v1/book/{id}", id);

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                );

        Mockito.verifyNoInteractions(bookService);
    }

    @Test
    @DisplayName("Update Book - Success")
    void update_withValidIdAndRequest_Returns200AndBookDto() throws Exception {
        Long id = 1L;
        String title = "Title";
        String author = "Author";
        Integer publicationYear = 1984;
        BookDto response = new BookDto(id, title, author, publicationYear);
        UpdateBookRequest request = new UpdateBookRequest(title, author, publicationYear);

        Mockito.when(bookService.update(id, request))
                .thenReturn(response);

        var requestBuilder = MockMvcRequestBuilders.patch("/api/v1/book/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        jsonPath("$.id").value(id),
                        jsonPath("$.title").value(title),
                        jsonPath("$.author").value(author),
                        jsonPath("$.publicationYear").value(publicationYear)
                );

        Mockito.verify(bookService).update(id, request);
    }

    @Test
    @DisplayName("Update Book - Partial Update")
    void update_WithOnlyTitle_Returns200() throws Exception {
        long id = 1L;
        var request = new UpdateBookRequest("New title", null, null);
        var response = new BookDto(id, "New title", "Existing author", 1900);

        Mockito.when(bookService.update(id, request))
                .thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/book/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(id),
                        jsonPath("$.title").value("New title"),
                        jsonPath("$.author").value("Existing author"),
                        jsonPath("$.publicationYear").value(1900)
                );

        Mockito.verify(bookService).update(id, request);
    }

    @Test
    @DisplayName("Update Book - Not Positive Id")
    void update_withNonPositiveId_Returns400() throws Exception {
        Long id = -1L;

        var request = new UpdateBookRequest("Title", "Author", 1900);
        var requestBuilder = MockMvcRequestBuilders.patch("/api/v1/book/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                );

        Mockito.verifyNoInteractions(bookService);
    }

    @Test
    @DisplayName("Update Book - Bad JSON")
    void update_WithBadJSON_Returns400() throws Exception {
        Long id = 1L;
        String malformedJson = "{" + "bad json" + "}";
        var requestBuilder = MockMvcRequestBuilders.patch("/api/v1/book/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(malformedJson);

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                );
        Mockito.verifyNoInteractions(bookService);
    }

    @Test
    @DisplayName("Update Book - Not Found")
    void update_WithNonExistingId_Returns404() throws Exception {
        Long id = 1L;
        String title = "Title";
        String author = "Author";
        Integer publicationYear = 1900;
        var request = new UpdateBookRequest(title, author, publicationYear);

        Mockito.when(bookService.update(id, request)).thenThrow(new BookNotFoundException(id));

        var requestBuilder = MockMvcRequestBuilders.patch("/api/v1/book/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isNotFound(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                );

        Mockito.verify(bookService).update(id, request);
    }

    @ParameterizedTest
    @MethodSource("invalidUpdateRequests")
    @DisplayName("Update Book - Invalid Request")
    void update_WithInvalidRequest_Returns400(UpdateBookRequest request) throws Exception {
        long id = 1L;

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/book/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(
                                MediaType.APPLICATION_PROBLEM_JSON
                        )
                );

        Mockito.verifyNoInteractions(bookService);
    }

    private static Stream<Arguments> invalidUpdateRequests() {
        return Stream.of(
                Arguments.of(new UpdateBookRequest("", null, null)),
                Arguments.of(new UpdateBookRequest("   ", null, null)),
                Arguments.of(new UpdateBookRequest(null, "", null)),
                Arguments.of(new UpdateBookRequest(null, "   ", null)),
                Arguments.of(new UpdateBookRequest(null, null, 0)),
                Arguments.of(new UpdateBookRequest(null, null, -1900))
        );
    }

    @Test
    @DisplayName("Delete Book - Success")
    void delete_WithValidId_Returns204() throws Exception {
        Long id = 1L;
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/book/{id}", id))
                .andExpectAll(
                        status().isNoContent()
                );

        Mockito.verify(bookService).delete(id);
    }

    @Test
    @DisplayName("Delete Book - Not Found")
    void delete_WithNonExistingId_Returns404() throws Exception {
        Long id = 1L;

        Mockito.doThrow(new BookNotFoundException(id))
                .when(bookService)
                .delete(id);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/book/{id}", id))
                .andExpectAll(
                        status().isNotFound(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                );

        Mockito.verify(bookService).delete(id);
    }

    @Test
    @DisplayName("Delete Book - Not Positive Id")
    void delete_WithNonPositiveId_Returns400() throws Exception {
        Long id = -1L;

        var requestBuilder = MockMvcRequestBuilders.delete("/api/v1/book/{id}", id);

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                );
        Mockito.verifyNoInteractions(bookService);
    }
}