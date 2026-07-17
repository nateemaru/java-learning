package ru.nateemaru.polygon.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.nateemaru.polygon.dto.request.CreateUserRequest;
import ru.nateemaru.polygon.dto.request.UpdateUserSummaryRequest;
import ru.nateemaru.polygon.dto.response.UserDto;
import ru.nateemaru.polygon.dto.response.UsersPageDto;
import ru.nateemaru.polygon.exception.UserNotFoundException;
import ru.nateemaru.polygon.service.user.UserService;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@DisplayName("UserControllerV1 tests")
@WebMvcTest(controllers = UserControllerV1.class)
class UserControllerV1Test {
    @MockitoBean
    UserService userService;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Create User - Success")
    void create_WithValidRequest_Returns201AndValidUserDto() throws Exception {
        Instant now = Instant.now();
        Long id = 1L;
        String name = "test";
        String email = "test@gmail.com";
        UserDto dto = new UserDto(id, name, email, now, now, null);
        Mockito.when(userService.create(name, email))
                .thenReturn(dto);
        var requestBuilder = MockMvcRequestBuilders.post("/api/v1/user/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(new CreateUserRequest(name, email)));

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isCreated(),
                        header().string("Location", "/api/v1/user/" + id),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        jsonPath("$.id").value(id),
                        jsonPath("$.name").value(name),
                        jsonPath("$.email").value(email),
                        jsonPath("$.orders").doesNotHaveJsonPath()
                );

        Mockito.verify(userService).create(name, email);
    }

    @ParameterizedTest
    @MethodSource("invalidCreateRequests")
    @DisplayName("Create User - Invalid Requests")
    void create_WithInvalidRequest_Returns400(CreateUserRequest request) throws Exception {
        var requestBuilder = MockMvcRequestBuilders.post("/api/v1/user/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request));

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                );
        Mockito.verifyNoInteractions(this.userService);
    }

    @Test
    @DisplayName("Create User - Missing Required Fields")
    void create_WithMissingFields_Returns400() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.post("/api/v1/user/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}");

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                );
        Mockito.verifyNoInteractions(this.userService);
    }

    @Test
    @DisplayName("Create User - Bad JSON")
    void create_WithBadJSON_Returns400() throws Exception {
        String malformedJson = "{" + "bad json" + "}";
        var requestBuilder = MockMvcRequestBuilders.post("/api/v1/user/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(malformedJson);

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                );
        Mockito.verifyNoInteractions(this.userService);
    }

    private static Stream<Arguments> invalidCreateRequests() {
        return Stream.of(
                Arguments.of(new CreateUserRequest(null, "test@gmail.com")),
                Arguments.of(new CreateUserRequest("test", null)),
                Arguments.of(new CreateUserRequest(null, null)),
                Arguments.of(new CreateUserRequest("test", "bad_email_format")),
                Arguments.of(new CreateUserRequest("", "test@gmail.com")),
                Arguments.of(new CreateUserRequest("test", ""))
        );
    }

    @Test
    @DisplayName("Delete User - Success")
    void delete_WithValidId_Returns204() throws Exception {
        Long id = 1L;
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/user/{id}", id))
                .andExpectAll(
                        status().isNoContent()
                );

        Mockito.verify(userService).delete(id);
    }

    @Test
    @DisplayName("Delete User - Not Found")
    void delete_WithNotExistingId_Returns404() throws Exception {
        Long id = 1L;

        Mockito.doThrow(new UserNotFoundException(id))
                .when(userService)
                .delete(id);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/user/{id}", id))
                .andExpectAll(
                        status().isNotFound(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                );

        Mockito.verify(userService).delete(id);
    }

    @Test
    @DisplayName("Delete User - Not Positive Id")
    void delete_WithNegativeId_Returns400() throws Exception {
        Long id = -1L;

        var requestBuilder = MockMvcRequestBuilders.delete("/api/v1/user/{id}", id);

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                );
        Mockito.verifyNoInteractions(this.userService);
    }

    @Test
    @DisplayName("Update User - Success")
    void update_WithValidIdAndRequest_Returns200AndUserDto() throws Exception {
        Instant now = Instant.now();
        Long id = 1L;
        String name = "updated_test";
        String email = "updated_test@gmail.com";
        UserDto dto = new UserDto(id, name, email, now, now, null);
        Mockito.when(userService.update(id, name, email))
                .thenReturn(dto);

        var requestBuilder = MockMvcRequestBuilders
                .put("/api/v1/user/{id}", id)
                .content(asJsonString(new UpdateUserSummaryRequest(name, email)))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        jsonPath("$.id").value(id),
                        jsonPath("$.name").value(name),
                        jsonPath("$.email").value(email),
                        jsonPath("$.orders").doesNotHaveJsonPath()
                );

        Mockito.verify(userService).update(id, name, email);
    }

    @ParameterizedTest
    @MethodSource("invalidUpdateRequests")
    @DisplayName("Update User - Invalid Requests")
    void update_WithInvalidRequests_Returns400(UpdateUserSummaryRequest request) throws Exception {
        Long id = 1L;
        var requestBuilder = MockMvcRequestBuilders.put("/api/v1/user/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request));

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                );
        Mockito.verifyNoInteractions(this.userService);
    }

    private static Stream<Arguments> invalidUpdateRequests() {
        return Stream.of(
                Arguments.of(new UpdateUserSummaryRequest(null, "test@gmail.com")),
                Arguments.of(new UpdateUserSummaryRequest("test", null)),
                Arguments.of(new UpdateUserSummaryRequest(null, null)),
                Arguments.of(new UpdateUserSummaryRequest("test", "bad_email_format")),
                Arguments.of(new UpdateUserSummaryRequest("", "test@gmail.com")),
                Arguments.of(new UpdateUserSummaryRequest("test", ""))
        );
    }

    @Test
    @DisplayName("Update User - Missing Required Fields")
    void update_WithMissingFields_Returns400() throws Exception {
        Long id = 1L;
        var requestBuilder = MockMvcRequestBuilders.put("/api/v1/user/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}");

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                );
        Mockito.verifyNoInteractions(this.userService);
    }

    @Test
    @DisplayName("Update User - Bad JSON")
    void update_WithBadJSON_Returns400() throws Exception {
        Long id = 1L;
        String malformedJson = "{" + "bad json" + "}";
        var requestBuilder = MockMvcRequestBuilders.put("/api/v1/user/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(malformedJson);

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                );
        Mockito.verifyNoInteractions(this.userService);
    }

    @Test
    @DisplayName("Update User - Not Found")
    void update_WithNotExistingId_Returns404() throws Exception {
        Long id = 1L;
        String name = "updated_test";
        String email = "updated_test@gmail.com";

        Mockito.doThrow(new UserNotFoundException(id))
                .when(userService)
                .update(id, name, email);

        var requestBuilder = MockMvcRequestBuilders.put("/api/v1/user/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(new UpdateUserSummaryRequest(name, email)));

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isNotFound(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                );

        Mockito.verify(userService).update(id, name, email);
    }

    @Test
    @DisplayName("Update User - Negative Id")
    void update_WhenIdIsNonPositive_Returns404() throws Exception {
        Long id = -1L;

        UpdateUserSummaryRequest request =
                new UpdateUserSummaryRequest(
                        "test",
                        "test@gmail.com"
                );

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/user/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isBadRequest());

        Mockito.verifyNoInteractions(userService);
    }

    @Test
    @DisplayName("User With Details - User Dto With Orders")
    void userWithOrders_WithExistingId_Returns200AndUserDtoWithOrders() throws Exception {
        Instant now = Instant.now();
        Long id = 1L;
        String name = "test";
        String email = "test@gmail.com";
        UserDto dto = new UserDto(id, name, email, now, now, Collections.emptyList());
        var requestBuilder = MockMvcRequestBuilders.get("/api/v1/user/{id}", id)
                .accept(MediaType.APPLICATION_JSON);

        Mockito.when(this.userService.getWithOrders(id))
                .thenReturn(dto);

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        jsonPath("$.id").value(id),
                        jsonPath("$.name").value(name),
                        jsonPath("$.email").value(email),
                        jsonPath("$.orders").hasJsonPath()
                );
        Mockito.verify(this.userService).getWithOrders(id);
    }

    @Test
    @DisplayName("User With Details - Not Found")
    void userWithOrders_WithNotExistingId_Returns404() throws Exception {
        Long id = 1L;

        Mockito.doThrow(new UserNotFoundException(id))
                .when(userService)
                .getWithOrders(id);

        var requestBuilder = MockMvcRequestBuilders.get("/api/v1/user/{id}", id)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isNotFound(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                );

        Mockito.verify(userService).getWithOrders(id);
    }

    @Test
    @DisplayName("User With Details - Not Positive Id")
    void userWithOrders_WithInvalidIdType_Returns400() throws Exception {
        Long id = -1L;

        var requestBuilder = MockMvcRequestBuilders.get("/api/v1/user/{id}", id);

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                );
        Mockito.verifyNoInteractions(this.userService);
    }

    @Test
    @DisplayName("Search Users - Custom Parameters")
    void search_WithCustomParameters_Returns200AndPageDto() throws Exception {
        UsersPageDto response = new UsersPageDto(
                List.of(),
                2,
                10,
                0,
                0,
                true,
                true
        );

        Mockito.when(userService.getPage(Mockito.any(Pageable.class)))
                .thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/user/search")
                        .param("page", "2")
                        .param("size", "10")
                        .param("sortBy", "EMAIL")
                        .param("direction", "DESC"))
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        jsonPath("$.users").isArray(),
                        jsonPath("$.page").value(2),
                        jsonPath("$.size").value(10),
                        jsonPath("$.totalElements").value(0),
                        jsonPath("$.totalPages").value(0),
                        jsonPath("$.first").value(true),
                        jsonPath("$.last").value(true)
                );

        Pageable expected = PageRequest.of(
                2,
                10,
                Sort.by(Sort.Direction.DESC, "email")
        );

        Mockito.verify(userService).getPage(expected);
    }

    @Test
    @DisplayName("Search Users - Default Parameters")
    void search_WithoutParameters_Returns200AndPageDto() throws Exception {
        UsersPageDto response = new UsersPageDto(
                List.of(),
                0,
                20,
                0,
                0,
                true,
                true
        );

        Mockito.when(userService.getPage(Mockito.any(Pageable.class)))
                .thenReturn(response);

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/user/search")
                )
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        jsonPath("$.users").isArray(),
                        jsonPath("$.page").value(0),
                        jsonPath("$.size").value(20),
                        jsonPath("$.totalElements").value(0),
                        jsonPath("$.totalPages").value(0),
                        jsonPath("$.first").value(true),
                        jsonPath("$.last").value(true)
                );

        Pageable expected = PageRequest.of(
                0,
                20,
                Sort.by(Sort.Direction.ASC, "id")
        );

        Mockito.verify(userService).getPage(expected);
    }

    @Test
    @DisplayName("Search Users - Negative Page")
    void search_WithNegativePage_Returns400() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/user/search")
                        .param("page", "-1"))
                .andExpect(status().isBadRequest());

        Mockito.verifyNoInteractions(userService);
    }

    @Test
    @DisplayName("Search Users - Zero Size")
    void search_WithZeroSize_Returns400() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/user/search")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());

        Mockito.verifyNoInteractions(userService);
    }

    @Test
    @DisplayName("Search Users - Invalid Sort Field")
    void search_WithInvalidSortField_Returns400() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/user/search")
                        .param("sortBy", "PASSWORD"))
                .andExpect(status().isBadRequest());

        Mockito.verifyNoInteractions(userService);
    }

    @Test
    @DisplayName("Search Users - Invalid Direction")
    void search_WithInvalidDirection_Returns400() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/user/search")
                        .param("direction", "SIDEWAYS"))
                .andExpect(status().isBadRequest());

        Mockito.verifyNoInteractions(userService);
    }

    @ParameterizedTest
    @CsvSource({
            "ID, id",
            "NAME, name",
            "EMAIL, email",
            "CREATED_AT, createdAt"
    })
    @DisplayName("Search Users - Valid Sort Type")
    void search_WhenSortFieldValid_Returns200AndPageDto(String sortBy, String expectedProperty
    ) throws Exception {
        UsersPageDto response = new UsersPageDto(
                List.of(),
                0,
                20,
                0,
                0,
                true,
                true
        );

        Mockito.when(userService.getPage(Mockito.any(Pageable.class)))
                .thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/user/search")
                        .param("sortBy", sortBy)
                        .param("direction", "DESC"))
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        jsonPath("$.users").isArray(),
                        jsonPath("$.page").value(0),
                        jsonPath("$.size").value(20),
                        jsonPath("$.totalElements").value(0),
                        jsonPath("$.totalPages").value(0),
                        jsonPath("$.first").value(true),
                        jsonPath("$.last").value(true)
                );

        Pageable expected = PageRequest.of(
                0,
                20,
                Sort.by(Sort.Direction.DESC, expectedProperty)
        );

        Mockito.verify(userService).getPage(expected);
    }

    @Test
    @DisplayName("Search Users - Orders Are Hidden")
    void search_WhenUsersReturned_DoesNotIncludeOrders() throws Exception {
        Instant now = Instant.now();

        UserDto user = new UserDto(
                1L,
                "test",
                "test@gmail.com",
                now,
                now,
                List.of()
        );

        UsersPageDto response = new UsersPageDto(
                List.of(user),
                0,
                20,
                1,
                1,
                true,
                true
        );

        Pageable expected = PageRequest.of(
                0,
                20,
                Sort.by(Sort.Direction.ASC, "id")
        );

        Mockito.when(userService.getPage(expected))
                .thenReturn(response);

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/user/search")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        jsonPath("$.users").isArray(),
                        jsonPath("$.users.length()").value(1),
                        jsonPath("$.users[0].id").value(1L),
                        jsonPath("$.users[0].name").value("test"),
                        jsonPath("$.users[0].email").value("test@gmail.com"),
                        jsonPath("$.users[0].orders").doesNotHaveJsonPath()
                );

        Mockito.verify(userService).getPage(expected);
    }

    private String asJsonString(final Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}