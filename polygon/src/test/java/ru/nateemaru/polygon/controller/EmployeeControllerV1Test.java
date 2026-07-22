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
import ru.nateemaru.polygon.dto.request.CreateEmployeeRequest;
import ru.nateemaru.polygon.dto.request.UpdateEmployeeRequest;
import ru.nateemaru.polygon.dto.response.EmployeeDto;
import ru.nateemaru.polygon.dto.response.EmployeePreviewDto;
import ru.nateemaru.polygon.entity.EmployeePosition;
import ru.nateemaru.polygon.exception.EmployeeNotFoundException;
import ru.nateemaru.polygon.service.employee.EmployeeService;
import tools.jackson.databind.ObjectMapper;

import java.util.stream.Stream;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("EmployeeControllerV1 tests")
@WebMvcTest(controllers = EmployeeControllerV1.class)
class EmployeeControllerV1Test {
    @MockitoBean
    private EmployeeService service;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Create Employee - Success")
    void create_WithValidRequest_Returns201AndValidDto() throws Exception {
        Long id = 1L;
        String firstName = "firstName";
        String lastName = "lastName";
        EmployeePosition position = EmployeePosition.Developer;
        Integer salary = 2000;
        Long departmentId = 1L;
        EmployeeDto response = new EmployeeDto(id, firstName, lastName, position, salary, departmentId);
        CreateEmployeeRequest request = new CreateEmployeeRequest(firstName, lastName, position, salary, departmentId);

        Mockito.when(service.create(request.toCommand()))
                .thenReturn(response);

        var requestBuilder = MockMvcRequestBuilders.post("/api/v1/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));

        String expectedLocation =
                "http://localhost/api/v1/employees/" + id;

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isCreated(),
                        header().string("Location", expectedLocation),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        jsonPath("$.id").value(id),
                        jsonPath("$.firstName").value(firstName),
                        jsonPath("$.lastName").value(lastName),
                        jsonPath("$.position").value(position.name()),
                        jsonPath("$.salary").value(salary),
                        jsonPath("$.departmentId").value(departmentId)
                );

        Mockito.verify(service).create(request.toCommand());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidCreateRequests")
    @DisplayName("Create Employee - Invalid Request")
    void create_WithInvalidRequest_Returns400(String description, CreateEmployeeRequest request) throws Exception {
        var requestBuilder = MockMvcRequestBuilders.post("/api/v1/employees")
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
        String firstName = "firstName";
        String lastName = "lastName";
        EmployeePosition position = EmployeePosition.Developer;
        Integer salary = 2000;
        Long departmentId = 1L;

        return Stream.of(
                Arguments.of("null first name", new CreateEmployeeRequest(null, lastName, position, salary, departmentId)),
                Arguments.of("empty first name", new CreateEmployeeRequest("", lastName, position, salary, departmentId)),
                Arguments.of("blank first name", new CreateEmployeeRequest("   ", lastName, position, salary, departmentId)),
                Arguments.of("null last name", new CreateEmployeeRequest(firstName, null, position, salary, departmentId)),
                Arguments.of("empty last name", new CreateEmployeeRequest(firstName, "", position, salary, departmentId)),
                Arguments.of("blank last name", new CreateEmployeeRequest(firstName, "   ", position, salary, departmentId)),
                Arguments.of("null position", new CreateEmployeeRequest(firstName, lastName, null, salary, departmentId)),
                Arguments.of("null salary", new CreateEmployeeRequest(firstName, lastName, position, null, departmentId)),
                Arguments.of("negative salary", new CreateEmployeeRequest(firstName, lastName, position, -2000, departmentId)),
                Arguments.of("negative department id", new CreateEmployeeRequest(firstName, lastName, position, salary, -1000L)),
                Arguments.of("zero department id", new CreateEmployeeRequest(firstName, lastName, position, salary, 0L))
                );
    }

    @Test
    @DisplayName("Create Employee - Missing Required Fields")
    void create_WithMissingFields_Returns400() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.post("/api/v1/employees")
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
    @DisplayName("Create Employee - Bad JSON")
    void create_WithBadJSON_Returns400() throws Exception {
        String malformedJson = "{" + "bad json" + "}";
        var requestBuilder = MockMvcRequestBuilders.post("/api/v1/employees")
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
    @DisplayName("Get Employee Preview - Success")
    void employeePreview_withValidId_Returns200AndValidDto() throws Exception {
        Long id = 1L;
        String fullName = "Firstname Lastname";
        EmployeePosition position = EmployeePosition.Developer;
        String departmentName = "Department name";
        EmployeePreviewDto result = new EmployeePreviewDto(fullName, position, departmentName);

        var requestBuilder = MockMvcRequestBuilders.get("/api/v1/employees/{id}", id)
                .accept(MediaType.APPLICATION_JSON);

        Mockito.when(service.getPreviewById(id)).thenReturn(result);

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        jsonPath("$.fullName").value(fullName),
                        jsonPath("$.position").value(position.name()),
                        jsonPath("$.departmentName").value(departmentName)
                );

        Mockito.verify(service).getPreviewById(id);
    }

    @Test
    @DisplayName("Get Employee Preview - Not Found")
    void employeePreview_WithNonExistingId_Returns404() throws Exception {
        Long id = 1L;

        Mockito.when(service.getPreviewById(id)).thenThrow(new EmployeeNotFoundException(id));

        var requestBuilder = MockMvcRequestBuilders.get("/api/v1/employees/{id}", id)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isNotFound(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                );

        Mockito.verify(service).getPreviewById(id);
    }

    @Test
    @DisplayName("Get Employee Preview - Not Positive Id")
    void employeePreview_withNotPositiveId_Returns400() throws Exception {
        Long id = -1L;

        var requestBuilder = MockMvcRequestBuilders.get("/api/v1/employees/{id}", id);

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                );

        Mockito.verifyNoInteractions(service);
    }

    @Test
    @DisplayName("Update Employee - All Fields Success")
    void update_withValidIdAndRequest_Returns200AndDto() throws Exception {
        Long id = 1L;
        String firstName = "firstName";
        String lastName = "lastName";
        EmployeePosition position = EmployeePosition.Developer;
        Integer salary = 2000;
        Long departmentId = 1L;
        EmployeeDto response = new EmployeeDto(id, firstName, lastName, position, salary, departmentId);
        UpdateEmployeeRequest request = new UpdateEmployeeRequest(firstName, lastName, position, salary, departmentId);

        Mockito.when(service.updateById(id, request.toCommand()))
                .thenReturn(response);

        var requestBuilder = MockMvcRequestBuilders.patch("/api/v1/employees/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        jsonPath("$.id").value(id),
                        jsonPath("$.firstName").value(firstName),
                        jsonPath("$.lastName").value(lastName),
                        jsonPath("$.position").value(position.name()),
                        jsonPath("$.salary").value(salary),
                        jsonPath("$.departmentId").value(departmentId)
                );

        Mockito.verify(service).updateById(id, request.toCommand());
    }

    @Test
    @DisplayName("Update Employee - Empty Patch Is Accepted")
    void update_withValidIdAndNotChangedFields_Returns200AndDto() throws Exception {
        Long id = 1L;
        String firstName = "firstName";
        String lastName = "lastName";
        EmployeePosition position = EmployeePosition.Developer;
        Integer salary = 2000;
        Long departmentId = 1L;
        EmployeeDto response = new EmployeeDto(id, firstName, lastName, position, salary, departmentId);
        UpdateEmployeeRequest request = new UpdateEmployeeRequest(null, null, null, null, null);

        Mockito.when(service.updateById(id, request.toCommand()))
                .thenReturn(response);

        var requestBuilder = MockMvcRequestBuilders.patch("/api/v1/employees/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        jsonPath("$.id").value(id),
                        jsonPath("$.firstName").value(firstName),
                        jsonPath("$.lastName").value(lastName),
                        jsonPath("$.position").value(position.name()),
                        jsonPath("$.salary").value(salary),
                        jsonPath("$.departmentId").value(departmentId)
                );

        Mockito.verify(service).updateById(id, request.toCommand());
    }

    @Test
    @DisplayName("Update Employee - Not Positive Id")
    void update_withNonPositiveId_Returns400() throws Exception {
        Long id = -1L;
        String firstName = "firstName";
        String lastName = "lastName";
        EmployeePosition position = EmployeePosition.Developer;
        Integer salary = 2000;
        Long departmentId = 1L;
        UpdateEmployeeRequest request = new UpdateEmployeeRequest(firstName, lastName, position, salary, departmentId);
        var requestBuilder = MockMvcRequestBuilders.patch("/api/v1/employees/{id}", id)
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
    @DisplayName("Update Employee - Bad JSON")
    void update_WithBadJSON_Returns400() throws Exception {
        Long id = 1L;
        String malformedJson = "{" + "bad json" + "}";
        var requestBuilder = MockMvcRequestBuilders.patch("/api/v1/employees/{id}", id)
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
    @DisplayName("Update Employee - Not Found")
    void update_WithNonExistingId_Returns404() throws Exception {
        Long id = 1L;
        String firstName = "firstName";
        String lastName = "lastName";
        EmployeePosition position = EmployeePosition.Developer;
        Integer salary = 2000;
        Long departmentId = 1L;
        UpdateEmployeeRequest request = new UpdateEmployeeRequest(firstName, lastName, position, salary, departmentId);

        Mockito.when(service.updateById(id, request.toCommand()))
                .thenThrow(new EmployeeNotFoundException(id));

        var requestBuilder = MockMvcRequestBuilders.patch("/api/v1/employees/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isNotFound(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                );

        Mockito.verify(service).updateById(id, request.toCommand());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidUpdateRequests")
    @DisplayName("Update Employee - Invalid Request")
    void update_WithInvalidUpdateRequest_Returns400(String description, UpdateEmployeeRequest request) throws Exception {
        Long id = 1L;
        var requestBuilder = MockMvcRequestBuilders.patch("/api/v1/employees/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                );

        Mockito.verifyNoInteractions(this.service);
    }

    private static Stream<Arguments> invalidUpdateRequests() {
        String firstName = "firstName";
        String lastName = "lastName";
        EmployeePosition position = EmployeePosition.Developer;
        Integer salary = 2000;
        Long departmentId = 1L;

        return Stream.of(
                Arguments.of("empty first name", new UpdateEmployeeRequest("", lastName, position, salary, departmentId)),
                Arguments.of("blank first name", new UpdateEmployeeRequest("   ", lastName, position, salary, departmentId)),
                Arguments.of("empty last name", new UpdateEmployeeRequest(firstName, "", position, salary, departmentId)),
                Arguments.of("blank last name", new UpdateEmployeeRequest(firstName, "   ", position, salary, departmentId)),
                Arguments.of("negative salary", new UpdateEmployeeRequest(firstName, lastName, position, -1000, departmentId)),
                Arguments.of("negative departmentId", new UpdateEmployeeRequest(firstName, lastName, position, salary, -1L)),
                Arguments.of("zero departmentId", new UpdateEmployeeRequest(firstName, lastName, position, salary, 0L))
        );
    }

    @Test
    @DisplayName("Delete Employee - Success")
    void delete_WithValidId_Returns204() throws Exception {
        Long id = 1L;
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/employees/{id}", id))
                .andExpectAll(
                        status().isNoContent()
                );

        Mockito.verify(service).deleteById(id);
    }

    @Test
    @DisplayName("Delete Employee - Not Found")
    void delete_WithNonExistingId_Returns404() throws Exception {
        Long id = 1L;

        Mockito.doThrow(new EmployeeNotFoundException(id))
                .when(service)
                .deleteById(id);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/employees/{id}", id))
                .andExpectAll(
                        status().isNotFound(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                );

        Mockito.verify(service).deleteById(id);
    }

    @Test
    @DisplayName("Delete Employee - Not Positive Id")
    void delete_WithNonPositiveId_Returns400() throws Exception {
        Long id = -1L;

        var requestBuilder = MockMvcRequestBuilders.delete("/api/v1/employees/{id}", id);

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                );
        Mockito.verifyNoInteractions(service);
    }
}