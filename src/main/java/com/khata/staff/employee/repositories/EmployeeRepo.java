package com.khata.staff.employee.repositories;

import com.khata.staff.employee.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepo extends JpaRepository<Employee, Integer> {

    Optional<Employee> findByIdAndCreatedUserId(Integer id, Integer createdUserId);

    Optional<Employee> findByEmployeeCodeAndCreatedUserId(String employeeCode, Integer createdUserId);

    Optional<Employee> findByPhoneNumberAndCreatedUserId(String phoneNumber, Integer createdUserId);

    Page<Employee> findByCreatedUserId(Integer createdUserId, Pageable pageable);

    List<Employee> findAllByCreatedUserIdOrderByEmployeeCodeAsc(Integer createdUserId);

    Page<Employee> findByFullNameContainingIgnoreCaseAndCreatedUserId(
            String fullName,
            Integer createdUserId,
            Pageable pageable);
}
