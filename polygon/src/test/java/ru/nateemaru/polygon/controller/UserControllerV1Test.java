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
import ru.nateemaru.polygon.dto.request.CreateUserRequest;
import ru.nateemaru.polygon.dto.request.UpdateUserSummaryRequest;
import ru.nateemaru.polygon.dto.response.UserDto;
import ru.nateemaru.polygon.exception.UserNotFoundException;
import ru.nateemaru.polygon.service.user.UserService;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.Collections;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@DisplayName("UserControllerV1 tests")
@WebMvcTest(controllers = UserControllerV1.class)
class UserControllerV1Test {
    @MockitoBean
    UserService userService;
    @Autowired
    MockMvc mockMvc;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Create User - Success")
    void create_Returns201() throws Exception {
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
                        jsonPath("$.orders").doesNotExist()
                );

        Mockito.verify(userService).create(name, email);
    }

    @ParameterizedTest
    @MethodSource("invalidCreateRequests")
    @DisplayName("Create User - Invalid Requests")
    void create_withInvalidRequest_Returns400(CreateUserRequest request) throws Exception {
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
                .content("{\"bad\": \"json\"}");

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
    void delete_Returns204() throws Exception {
        Long id = 1L;
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/user/{id}", id))
                .andExpectAll(
                        status().isNoContent()
                );

        Mockito.verify(userService).delete(id);
    }

    @Test
    @DisplayName("Delete User - Not Found")
    void delete_Returns404() throws Exception {
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
    void delete_Returns400() throws Exception {
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
    void update_Returns200() throws Exception {
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
                        jsonPath("$.orders").doesNotExist()
                );

        Mockito.verify(userService).update(id, name, email);
    }

    @ParameterizedTest
    @MethodSource("invalidUpdateRequests")
    @DisplayName("Update User - Invalid Requests")
    void update_Returns400(UpdateUserSummaryRequest request) throws Exception {
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
                .content("{\"bad\": \"json\"}");

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
    void update_Returns404() throws Exception {
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
    @DisplayName("User With Details - Existing Id")
    void userWithOrders_WithExistingId_Returns200() throws Exception {
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
                        jsonPath("$.orders").exists()
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

    private static String asJsonString(final Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}