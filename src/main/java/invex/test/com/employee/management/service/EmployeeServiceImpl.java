package invex.test.com.employee.management.service;

import invex.test.com.database.dto.employee.EmployeeCreateRequest;
import invex.test.com.database.dto.employee.EmployeeResponse;
import invex.test.com.database.entity.employee.Employee;
import invex.test.com.database.exception.employee.EmployeeAlreadyExistsException;
import invex.test.com.database.exception.employee.EmployeeNotFoundException;
import invex.test.com.database.repository.employee.EmployeeRepository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Transactional
    @Override
    public void createEmployees(List<EmployeeCreateRequest> requests) {
        log.info("Creating {} employees", requests.size());

        List<String> names = requests.stream().map(EmployeeCreateRequest::getFirstName).toList();
        List<Employee> existing = employeeRepository.findByFirstNameIn(names);

        if (!existing.isEmpty()) {
            List<String> conflicts = existing.stream().map(Employee::getFirstName).toList();
            throw new EmployeeAlreadyExistsException("Some employees already exist", conflicts);
        }

        List<Employee> employees = requests.stream()
                .map(req -> Employee.builder()
                        .firstName(req.getFirstName())
                        .middleName(req.getMiddleName())
                        .lastNameFather(req.getLastNameFather())
                        .lastNameMother(req.getLastNameMother())
                        .age(req.getAge())
                        .gender(req.getGender())
                        .birthDate(LocalDate.parse(req.getBirthDate(), DateTimeFormatter.ofPattern("dd-MM-yyyy")))
                        .position(req.getPosition())
                        .enabled(true)
                        .deleted(false)
                        .build())
                .collect(Collectors.toList());

        employeeRepository.saveAll(employees);
    }

    @Override
    public List<EmployeeResponse> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    public EmployeeResponse getEmployeeById(String id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with ID: " + id));
        return convertToResponse(employee);
    }

    @Override
    public List<EmployeeResponse> searchEmployees(String name) {
        return employeeRepository.searchEmployees(name).stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Transactional
    @Override
    public EmployeeResponse updateEmployee(String id, EmployeeCreateRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with ID: " + id));

        employee.setFirstName(request.getFirstName());
        employee.setMiddleName(request.getMiddleName());
        employee.setLastNameFather(request.getLastNameFather());
        employee.setLastNameMother(request.getLastNameMother());
        employee.setAge(request.getAge());
        employee.setGender(request.getGender());
        employee.setBirthDate(LocalDate.parse(request.getBirthDate(), DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        employee.setPosition(request.getPosition());

        employee = employeeRepository.save(employee);
        return convertToResponse(employee);
    }

    @Transactional
    @Override
    public void deleteEmployee(String id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with ID: " + id));
        employeeRepository.delete(employee);
    }

    private EmployeeResponse convertToResponse(Employee employee) {
        return EmployeeResponse.builder()
                .id(employee.getId())
                .firstName(employee.getFirstName())
                .middleName(employee.getMiddleName())
                .lastNameFather(employee.getLastNameFather())
                .lastNameMother(employee.getLastNameMother())
                .age(employee.getAge())
                .gender(employee.getGender())
                .birthDate(employee.getBirthDate())
                .position(employee.getPosition())
                .enabled(employee.getEnabled())
                .build();
    }

}
