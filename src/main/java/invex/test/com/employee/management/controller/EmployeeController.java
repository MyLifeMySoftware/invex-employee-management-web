package invex.test.com.employee.management.controller;

import invex.test.com.database.dto.ApiResponse;
import invex.test.com.database.dto.employee.EmployeeCreateRequest;
import invex.test.com.database.dto.employee.EmployeeResponse;
import invex.test.com.employee.management.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SecurityRequirement(name = "ApiKeyAuth")
@Slf4j
@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
@Tag(name = "Employee Management", description = "Employee registration and management operations")
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    @Operation(summary = "Get all employees", description = "Retrieve all employees registered in the system")
    public ResponseEntity<ApiResponse<List<EmployeeResponse>>> getAllEmployees() {
        List<EmployeeResponse> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(ApiResponse.success("Employees retrieved successfully", employees));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get employee by ID", description = "Retrieve employee details by ID")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getEmployeeById(@PathVariable String id) {
        EmployeeResponse employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(ApiResponse.success("Employee retrieved successfully", employee));
    }

    @PostMapping
    @Operation(summary = "Create employees", description = "Insert one or multiple employees")
    public ResponseEntity<ApiResponse<List<EmployeeResponse>>> createEmployees(
            @Valid @RequestBody List<EmployeeCreateRequest> requests
    ) {
        employeeService.createEmployees(requests);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update employee", description = "Update all or some fields of an employee")
    public ResponseEntity<ApiResponse<EmployeeResponse>> updateEmployee(
            @PathVariable String id,
            @Valid @RequestBody EmployeeCreateRequest request
    ) {
        EmployeeResponse employee = employeeService.updateEmployee(id, request);
        return ResponseEntity.ok(ApiResponse.success("Employee updated successfully", employee));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete employee", description = "Delete an employee by ID")
    public ResponseEntity<Void> deleteEmployee(@PathVariable String id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Search employees", description = "Search employees by name")
    public ResponseEntity<ApiResponse<List<EmployeeResponse>>> searchEmployees(@RequestParam("name") String name) {
        List<EmployeeResponse> employees = employeeService.searchEmployees(name);
        return ResponseEntity.ok(ApiResponse.success("Employees retrieved successfully", employees));
    }

}
