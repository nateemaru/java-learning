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
import ru.nateemaru.polygon.dto.request.CreateDepartmentRequest;
import ru.nateemaru.polygon.dto.request.UpdateDepartmentRequest;
import ru.nateemaru.polygon.dto.response.DepartmentDto;
import ru.nateemaru.polygon.exception.DepartmentAlreadyExistsException;
import ru.nateemaru.polygon.exception.DepartmentNotFoundException;
import ru.nateemaru.polygon.service.department.DepartmentService;
import tools.jackson.databind.ObjectMapper;

import java.util.stream.Stream;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("DepartmentControllerV1 tests")
@WebMvcTest(controllers = DepartmentControllerV1.class)
class DepartmentControllerV1Test {
    @MockitoBean
    private DepartmentService service;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Create Department - Success")
    void create_WithValidRequest_Returns201AndValidDto() throws Exception {
        Long id = 1L;
        String name = "name";
        DepartmentDto response = new DepartmentDto(id, name);
        CreateDepartmentRequest request = new CreateDepartmentRequest(name);

        Mockito.when(service.create(request.toCommand()))
                .thenReturn(response);

        var requestBuilder = MockMvcRequestBuilders.post("/api/v1/departments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));

        String expectedLocation =
                "http://localhost/api/v1/departments/" + id;

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isCreated(),
                        header().string("Location", expectedLocation),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        jsonPath("$.id").value(id),
                        jsonPath("$.name").value(name)
                );

        Mockito.verify(service).create(request.toCommand());
    }

    @ParameterizedTest
    @MethodSource("invalidCreateRequests")
    @DisplayName("Create Department - Invalid Request")
    void create_WithInvalidRequest_Returns400(CreateDepartmentRequest request) throws Exception {
        var requestBuilder = MockMvcRequestBuilders.post("/api/v1/departments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                );

        Mockito.verifyNoInteractions(this.service);
    }

    private static Stream<Arguments> invalidCreateRequests() {
        return Stream.of(
                Arguments.of(new CreateDepartmentRequest(null)),
                Arguments.of(new CreateDepartmentRequest(" "))
        );
    }

    @Test
    @DisplayName("Create Department - Missing Required Fields")
    void create_WithMissingFields_Returns400() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.post("/api/v1/departments")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}");

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                );

        Mockito.verifyNoInteractions(this.service);
    }

    @Test
    @DisplayName("Create Department - Already Exists")
    void create_WithMissingFields_Returns409() throws Exception {
        String name = "name";
        CreateDepartmentRequest request = new CreateDepartmentRequest(name);

        Mockito.when(service.create(request.toCommand()))
                .thenThrow(DepartmentAlreadyExistsException.class);

        var requestBuilder = MockMvcRequestBuilders.post("/api/v1/departments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));


        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isConflict(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                );

        Mockito.verify(this.service).create(request.toCommand());
    }

    @Test
    @DisplayName("Create Department - Bad JSON")
    void create_WithBadJSON_Returns400() throws Exception {
        String malformedJson = "{" + "bad json" + "}";
        var requestBuilder = MockMvcRequestBuilders.post("/api/v1/departments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(malformedJson);

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                );

        Mockito.verifyNoInteractions(this.service);
    }

    @Test
    @DisplayName("Get Department - Success")
    void department_withValidId_Returns200AndValidDto() throws Exception {
        Long id = 1L;
        String name = "name";
        DepartmentDto result = new DepartmentDto(id, name);

        var requestBuilder = MockMvcRequestBuilders.get("/api/v1/departments/{id}", id)
                .accept(MediaType.APPLICATION_JSON);

        Mockito.when(service.getById(id)).thenReturn(result);

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        jsonPath("$.id").value(id),
                        jsonPath("$.name").value(name)
                );

        Mockito.verify(service).getById(id);
    }

    @Test
    @DisplayName("Get Department - Not Found")
    void department_WithNonExistingId_Returns404() throws Exception {
        Long id = 1L;

        Mockito.when(service.getById(id)).thenThrow(new DepartmentNotFoundException(id));

        var requestBuilder = MockMvcRequestBuilders.get("/api/v1/departments/{id}", id)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isNotFound(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                );

        Mockito.verify(service).getById(id);
    }

    @Test
    @DisplayName("Get Department - Not Positive Id")
    void department_withNotPositiveId_Returns400() throws Exception {
        Long id = -1L;

        var requestBuilder = MockMvcRequestBuilders.get("/api/v1/departments/{id}", id);

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                );

        Mockito.verifyNoInteractions(service);
    }

    @Test
    @DisplayName("Update Department - Success")
    void update_withValidIdAndRequest_Returns200AndDto() throws Exception {
        Long id = 1L;
        String name = "name";
        DepartmentDto response = new DepartmentDto(id, name);
        UpdateDepartmentRequest request = new UpdateDepartmentRequest(name);

        Mockito.when(service.updateById(id, request.toCommand()))
                .thenReturn(response);

        var requestBuilder = MockMvcRequestBuilders.patch("/api/v1/departments/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        jsonPath("$.id").value(id),
                        jsonPath("$.name").value(name)
                );

        Mockito.verify(service).updateById(id, request.toCommand());
    }

    @Test
    @DisplayName("Update Department - Not Positive Id")
    void update_withNonPositiveId_Returns400() throws Exception {
        Long id = -1L;
        String name = "name";
        var request = new UpdateDepartmentRequest(name);
        var requestBuilder = MockMvcRequestBuilders.patch("/api/v1/departments/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                );

        Mockito.verifyNoInteractions(service);
    }

    @Test
    @DisplayName("Update Department - Bad JSON")
    void update_WithBadJSON_Returns400() throws Exception {
        Long id = 1L;
        String malformedJson = "{" + "bad json" + "}";
        var requestBuilder = MockMvcRequestBuilders.patch("/api/v1/departments/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(malformedJson);

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                );
        Mockito.verifyNoInteractions(service);
    }

    @Test
    @DisplayName("Update Department - Not Found")
    void update_WithNonExistingId_Returns404() throws Exception {
        Long id = 1L;
        String name = "name";
        var request = new UpdateDepartmentRequest(name);

        Mockito.when(service.updateById(id, request.toCommand()))
                .thenThrow(new DepartmentNotFoundException(id));

        var requestBuilder = MockMvcRequestBuilders.patch("/api/v1/departments/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isNotFound(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                );

        Mockito.verify(service).updateById(id, request.toCommand());
    }

    @Test
    @DisplayName("Delete Department - Success")
    void delete_WithValidId_Returns204() throws Exception {
        Long id = 1L;
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/departments/{id}", id))
                .andExpectAll(
                        status().isNoContent()
                );

        Mockito.verify(service).deleteById(id);
    }

    @Test
    @DisplayName("Delete Department - Not Found")
    void delete_WithNonExistingId_Returns404() throws Exception {
        Long id = 1L;

        Mockito.doThrow(new DepartmentNotFoundException(id))
                .when(service)
                .deleteById(id);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/departments/{id}", id))
                .andExpectAll(
                        status().isNotFound(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                );

        Mockito.verify(service).deleteById(id);
    }

    @Test
    @DisplayName("Delete Department - Not Positive Id")
    void delete_WithNonPositiveId_Returns400() throws Exception {
        Long id = -1L;

        var requestBuilder = MockMvcRequestBuilders.delete("/api/v1/departments/{id}", id);

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                );
        Mockito.verifyNoInteractions(service);
    }
}