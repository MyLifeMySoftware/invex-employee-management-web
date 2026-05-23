package invex.test.com.employee.management.service;

import invex.test.com.database.dto.employee.EmployeeCreateRequest;
import invex.test.com.database.dto.employee.EmployeeResponse;
import java.util.List;

public interface EmployeeService {

    void createEmployees(List<EmployeeCreateRequest> requests);

    List<EmployeeResponse> getAllEmployees();

    EmployeeResponse getEmployeeById(String id);

    List<EmployeeResponse> searchEmployees(String name);

    EmployeeResponse updateEmployee(String id, EmployeeCreateRequest request);

    void deleteEmployee(String id);
}
