package com.example.testing_demo;

import com.example.testing_demo.entity.Employee;
import com.example.testing_demo.exception.EmployeeNotFoundException;
import com.example.testing_demo.repository.EmployeeRepository;
import com.example.testing_demo.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    @Test
    void getEmployeeById_shouldReturnEmployee_whenEmployeeExists() {
        // Arrange
        Employee employee = new Employee(
                1L, "Rahul", "rahul@example.com", "IT"
        );

        when(employeeRepository.findById(1L))
                .thenReturn(Optional.of(employee));

        // Act
        Employee result = employeeService.getEmployeeById(1L);

        // Assert
        assertEquals(1L, result.getId());
        assertEquals("Rahul", result.getName());
        assertEquals("rahul@example.com", result.getEmail());

        verify(employeeRepository).findById(1L);
    }

    @Test
    void createEmployee_shouldSaveEmployee() {
        Employee employee = new Employee(
                null, "Rahul", "rahul@example.com", "IT"
        );

        Employee savedEmployee = new Employee(
                1L, "Rahul", "rahul@example.com", "IT"
        );

        when(employeeRepository.save(any(Employee.class)))
                .thenReturn(savedEmployee);

        Employee result = employeeService.createEmployee(employee);

        assertEquals(1L, result.getId());
        assertEquals("Rahul", result.getName());

        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    void getAllEmployees_shouldReturnAllEmployees() {
        List<Employee> employees = List.of(
                new Employee(1L, "Rahul", "rahul@example.com", "IT"),
                new Employee(2L, "Priya", "priya@example.com", "HR")
        );

        when(employeeRepository.findAll()).thenReturn(employees);

        List<Employee> result = employeeService.getAllEmployees();

        assertEquals(2, result.size());
        assertEquals("Rahul", result.get(0).getName());

        verify(employeeRepository).findAll();
    }

    @Test
    void updateEmployee_shouldUpdateExistingEmployee() {
        Employee existingEmployee = new Employee(
                1L, "Rahul", "rahul@example.com", "IT"
        );

        Employee updatedEmployee = new Employee(
                null, "Rahul Kumar", "rahulk@example.com", "Finance"
        );

        when(employeeRepository.findById(1L))
                .thenReturn(Optional.of(existingEmployee));

        when(employeeRepository.save(any(Employee.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Employee result =
                employeeService.updateEmployee(1L, updatedEmployee);

        assertEquals("Rahul Kumar", result.getName());
        assertEquals("rahulk@example.com", result.getEmail());
        assertEquals("Finance", result.getDepartment());

        verify(employeeRepository).save(existingEmployee);
    }

    @Test
    void deleteEmployee_shouldDeleteExistingEmployee() {
        Employee employee = new Employee(
                1L, "Rahul", "rahul@example.com", "IT"
        );

        when(employeeRepository.findById(1L))
                .thenReturn(Optional.of(employee));

        employeeService.deleteEmployee(1L);

        verify(employeeRepository).delete(employee);
    }


    @Test
    void getEmployeeById_shouldThrowException_whenEmployeeDoesNotExist() {
        when(employeeRepository.findById(99L))
                .thenReturn(Optional.empty());

        EmployeeNotFoundException exception = assertThrows(
                EmployeeNotFoundException.class,
                () -> employeeService.getEmployeeById(99L)
        );

        assertEquals(
                "Employee not found with id: 99",
                exception.getMessage()
        );

        verify(employeeRepository).findById(99L);
    }


}