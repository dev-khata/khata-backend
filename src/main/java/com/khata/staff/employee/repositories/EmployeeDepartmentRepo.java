package com.khata.staff.employee.repositories;

import com.khata.staff.employee.entity.EmployeeDepartment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeDepartmentRepo extends JpaRepository<EmployeeDepartment, Integer> {

    List<EmployeeDepartment> findByPaymentTypeIsNull();
}
