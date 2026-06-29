package com.khata.staff.employee.service;

import com.khata.staff.employee.dto.EmployeeDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmployeeService {

    EmployeeDTO createEmployee(EmployeeDTO employeeDTO);

    EmployeeDTO updateEmployee(Integer employeeId, EmployeeDTO employeeDTO);

    EmployeeDTO getEmployeeById(Integer employeeId);

    Page<EmployeeDTO> getEmployees(Pageable pageable);

    Page<EmployeeDTO> searchEmployeeByName(String keyword, Pageable pageable);

    void deleteEmployee(Integer employeeId);

    byte[] exportEmployees();
}
