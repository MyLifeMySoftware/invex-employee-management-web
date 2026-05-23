package invex.test.com.employee.management.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import invex.test.com.database.dto.employee.EmployeeCreateRequest;
import invex.test.com.database.dto.employee.EmployeeResponse;
import invex.test.com.database.entity.employee.Employee;
import invex.test.com.database.exception.employee.EmployeeAlreadyExistsException;
import invex.test.com.database.exception.employee.EmployeeNotFoundException;
import invex.test.com.database.repository.employee.EmployeeRepository;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Employee Service Implementation Tests")
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private EmployeeCreateRequest buildRequest() {
        return EmployeeCreateRequest.builder()
                .firstName("Test")
                .middleName("Middle")
                .lastNameFather("Erick")
                .lastNameMother("Smith")
                .age(30)
                .gender("Male")
                .birthDate("01-01-1999")
                .position("Developer")
                .build();
    }

    private Employee buildEmployee(String id) {
        return Employee.builder()
                .id(id)
                .firstName("Test")
                .middleName("Middle")
                .lastNameFather("Erick")
                .lastNameMother("Smith")
                .age(30)
                .gender("Male")
                .birthDate(LocalDate.of(1999, 1, 1))
                .position("Developer")
                .enabled(true)
                .deleted(false)
                .build();
    }

    @Test
    @DisplayName("Should create employees successfully when no conflicts exist")
    void shouldCreateEmployeesSuccessfully() {
        EmployeeCreateRequest request = buildRequest();

        when(employeeRepository.findByFirstNameIn(List.of("Test")))
                .thenReturn(Collections.emptyList());

        employeeService.createEmployees(List.of(request));

        verify(employeeRepository).findByFirstNameIn(List.of("Test"));
        verify(employeeRepository).saveAll(anyList());
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    @DisplayName("Should throw EmployeeAlreadyExistsException when a firstName conflict is found")
    void shouldThrowEmployeeAlreadyExistsExceptionWhenDuplicate() {
        EmployeeCreateRequest request = buildRequest();

        when(employeeRepository.findByFirstNameIn(List.of("Test")))
                .thenReturn(List.of(buildEmployee("1")));

        assertThrows(EmployeeAlreadyExistsException.class,
                () -> employeeService.createEmployees(List.of(request)));

        verify(employeeRepository).findByFirstNameIn(List.of("Test"));
        verify(employeeRepository, never()).saveAll(anyList());
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    @DisplayName("Should create multiple employees successfully when no conflicts exist")
    void shouldCreateMultipleEmployeesSuccessfully() {
        EmployeeCreateRequest req1 = buildRequest();
        EmployeeCreateRequest req2 = EmployeeCreateRequest.builder()
                .firstName("Jane")
                .lastNameFather("Doe")
                .age(25)
                .gender("Female")
                .birthDate("15-06-2000")
                .position("Designer")
                .build();

        when(employeeRepository.findByFirstNameIn(List.of("Test", "Jane")))
                .thenReturn(Collections.emptyList());

        employeeService.createEmployees(List.of(req1, req2));

        verify(employeeRepository).findByFirstNameIn(List.of("Test", "Jane"));
        verify(employeeRepository).saveAll(anyList());
    }

    @Test
    @DisplayName("Should return all employees")
    void shouldReturnAllEmployees() {
        when(employeeRepository.findAll())
                .thenReturn(List.of(buildEmployee("1"), buildEmployee("2")));

        List<EmployeeResponse> result = employeeService.getAllEmployees();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(employeeRepository).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no employees exist")
    void shouldReturnEmptyListWhenNoEmployees() {
        when(employeeRepository.findAll()).thenReturn(Collections.emptyList());

        List<EmployeeResponse> result = employeeService.getAllEmployees();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Should return employee when found by ID")
    void shouldReturnEmployeeWhenFoundById() {
        when(employeeRepository.findById("1"))
                .thenReturn(Optional.of(buildEmployee("1")));

        EmployeeResponse response = employeeService.getEmployeeById("1");

        assertNotNull(response);
        assertEquals("1", response.getId());
        assertEquals("Test", response.getFirstName());
        verify(employeeRepository).findById("1");
    }

    @Test
    @DisplayName("Should throw EmployeeNotFoundException when ID does not exist")
    void shouldThrowEmployeeNotFoundExceptionWhenIdNotFound() {
        when(employeeRepository.findById("99"))
                .thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class,
                () -> employeeService.getEmployeeById("99"));

        verify(employeeRepository).findById("99");
    }

    @Test
    @DisplayName("Should return matching employees for search term")
    void shouldReturnMatchingEmployeesForSearchTerm() {
        when(employeeRepository.searchEmployees("Test"))
                .thenReturn(List.of(buildEmployee("1")));

        List<EmployeeResponse> result = employeeService.searchEmployees("Test");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test", result.get(0).getFirstName());
        verify(employeeRepository).searchEmployees("Test");
    }

    @Test
    @DisplayName("Should return empty list when no employees match search term")
    void shouldReturnEmptyListWhenNoMatchForSearchTerm() {
        when(employeeRepository.searchEmployees(anyString()))
                .thenReturn(Collections.emptyList());

        List<EmployeeResponse> result = employeeService.searchEmployees("Unknown");

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Should update employee successfully when found")
    void shouldUpdateEmployeeSuccessfully() {
        Employee existing = buildEmployee("1");
        EmployeeCreateRequest updateRequest = EmployeeCreateRequest.builder()
                .firstName("Updated")
                .middleName("Mid")
                .lastNameFather("Father")
                .lastNameMother("Mother")
                .age(35)
                .gender("Male")
                .birthDate("10-05-1990")
                .position("Manager")
                .build();

        when(employeeRepository.findById("1")).thenReturn(Optional.of(existing));
        when(employeeRepository.save(any(Employee.class))).thenReturn(existing);

        EmployeeResponse response = employeeService.updateEmployee("1", updateRequest);

        assertNotNull(response);
        verify(employeeRepository).findById("1");
        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    @DisplayName("Should throw EmployeeNotFoundException when updating non-existent employee")
    void shouldThrowWhenUpdatingNonExistentEmployee() {
        when(employeeRepository.findById("99")).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class,
                () -> employeeService.updateEmployee("99", buildRequest()));

        verify(employeeRepository).findById("99");
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    @DisplayName("Should delete employee successfully when found")
    void shouldDeleteEmployeeSuccessfully() {
        Employee existing = buildEmployee("1");
        when(employeeRepository.findById("1")).thenReturn(Optional.of(existing));

        employeeService.deleteEmployee("1");

        verify(employeeRepository).findById("1");
        verify(employeeRepository).delete(existing);
    }

    @Test
    @DisplayName("Should throw EmployeeNotFoundException when deleting non-existent employee")
    void shouldThrowWhenDeletingNonExistentEmployee() {
        when(employeeRepository.findById("99")).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class,
                () -> employeeService.deleteEmployee("99"));

        verify(employeeRepository).findById("99");
        verify(employeeRepository, never()).delete(any(Employee.class));
    }
}