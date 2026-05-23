package invex.test.com.employee.management.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import invex.test.com.database.dto.employee.EmployeeCreateRequest;
import invex.test.com.database.dto.employee.EmployeeResponse;
import invex.test.com.employee.management.service.EmployeeService;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
@DisplayName("Employee Controller Tests")
class EmployeeControllerTest {

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(employeeController).build();
        objectMapper = new ObjectMapper();
    }

    private EmployeeResponse buildResponse(String id) {
        return EmployeeResponse.builder()
                .id(id)
                .firstName("Test")
                .middleName("Middle")
                .lastNameFather("Father")
                .lastNameMother("Mother")
                .age(30)
                .gender("Male")
                .birthDate(LocalDate.of(1999, 1, 1))
                .position("Developer")
                .enabled(true)
                .build();
    }

    private EmployeeCreateRequest buildRequest() {
        return EmployeeCreateRequest.builder()
                .firstName("Test")
                .middleName("Middle")
                .lastNameFather("Father")
                .lastNameMother("Mother")
                .age(30)
                .gender("Male")
                .birthDate("01-01-1999")
                .position("Developer")
                .build();
    }

    @Test
    @DisplayName("GET /api/v1/employees - Should return 200 with list of employees")
    void shouldReturnAllEmployees() throws Exception {
        when(employeeService.getAllEmployees()).thenReturn(List.of(buildResponse("1")));

        mockMvc.perform(get("/api/v1/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value("1"))
                .andExpect(jsonPath("$.data[0].firstName").value("Test"));

        verify(employeeService).getAllEmployees();
    }

    @Test
    @DisplayName("GET /api/v1/employees - Should return 200 with empty list")
    void shouldReturnEmptyListWhenNoEmployees() throws Exception {
        when(employeeService.getAllEmployees()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());

        verify(employeeService).getAllEmployees();
    }

    @Test
    @DisplayName("GET /api/v1/employees/{id} - Should return 200 with employee")
    void shouldReturnEmployeeById() throws Exception {
        when(employeeService.getEmployeeById("1")).thenReturn(buildResponse("1"));

        mockMvc.perform(get("/api/v1/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value("1"))
                .andExpect(jsonPath("$.data.firstName").value("Test"))
                .andExpect(jsonPath("$.data.position").value("Developer"));

        verify(employeeService).getEmployeeById("1");
    }

    @Test
    @DisplayName("POST /api/v1/employees - Should return 200 when employees created")
    void shouldCreateEmployeesSuccessfully() throws Exception {
        doNothing().when(employeeService).createEmployees(anyList());

        String body = objectMapper.writeValueAsString(List.of(buildRequest()));

        mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());

        verify(employeeService).createEmployees(anyList());
    }

    @Test
    @DisplayName("PUT /api/v1/employees/{id} - Should return 200 with updated employee")
    void shouldUpdateEmployeeSuccessfully() throws Exception {
        EmployeeCreateRequest request = buildRequest();
        EmployeeResponse updated = buildResponse("1");

        when(employeeService.updateEmployee(eq("1"), any(EmployeeCreateRequest.class)))
                .thenReturn(updated);

        String body = objectMapper.writeValueAsString(request);

        mockMvc.perform(put("/api/v1/employees/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value("1"))
                .andExpect(jsonPath("$.data.firstName").value("Test"));

        verify(employeeService).updateEmployee(eq("1"), any(EmployeeCreateRequest.class));
    }

    @Test
    @DisplayName("DELETE /api/v1/employees/{id} - Should return 204 when employee deleted")
    void shouldDeleteEmployeeSuccessfully() throws Exception {
        doNothing().when(employeeService).deleteEmployee("1");

        mockMvc.perform(delete("/api/v1/employees/1"))
                .andExpect(status().isNoContent());

        verify(employeeService).deleteEmployee("1");
    }

    @Test
    @DisplayName("GET /api/v1/employees/search - Should return 200 with matching employees")
    void shouldSearchEmployeesSuccessfully() throws Exception {
        when(employeeService.searchEmployees("Test")).thenReturn(List.of(buildResponse("1")));

        mockMvc.perform(get("/api/v1/employees/search")
                        .param("name", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].firstName").value("Test"));

        verify(employeeService).searchEmployees("Test");
    }

    @Test
    @DisplayName("GET /api/v1/employees/search - Should return 200 with empty list when no match")
    void shouldReturnEmptyListWhenSearchFindsNothing() throws Exception {
        when(employeeService.searchEmployees(anyString())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/employees/search")
                        .param("name", "Unknown"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());

        verify(employeeService).searchEmployees("Unknown");
    }
}