package com.example.testing_demo;

import com.example.testing_demo.controller.EmployeeController;
import com.example.testing_demo.entity.Employee;
import com.example.testing_demo.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.mockito.Mockito.never;
@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmployeeService employeeService;

    @Test
    void getEmployeeById_shouldReturnEmployee() throws Exception {
        Employee employee = new Employee(
                1L,
                "Rahul",
                "rahul@example.com",
                "IT"
        );

        when(employeeService.getEmployeeById(1L))
                .thenReturn(employee);

        mockMvc.perform(get("/api/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Rahul"))
                .andExpect(jsonPath("$.email")
                        .value("rahul@example.com"))
                .andExpect(jsonPath("$.department").value("IT"));

        verify(employeeService).getEmployeeById(1L);
    }

    @Test
    void createEmployee_shouldReturnCreatedEmployee() throws Exception {
        Employee savedEmployee = new Employee(
                1L,
                "Rahul",
                "rahul@example.com",
                "IT"
        );

        when(employeeService.createEmployee(any(Employee.class)))
                .thenReturn(savedEmployee);

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "name": "Rahul",
                              "email": "rahul@example.com",
                              "department": "IT"
                            }
                            """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Rahul"));

        verify(employeeService).createEmployee(any(Employee.class));
    }

    @Test
    void createEmployee_shouldReturnBadRequest_whenNameIsBlank()
            throws Exception {

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "name": "",
                              "email": "rahul@example.com",
                              "department": "IT"
                            }
                            """))
                .andExpect(status().isBadRequest());

        verify(employeeService, never())
                .createEmployee(any(Employee.class));
    }


}
