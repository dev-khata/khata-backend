package com.khata.settings.department.repositories;

import com.khata.settings.department.entity.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DepartmentRepo extends JpaRepository<Department, Integer> {
    boolean existsByDepartmentCodeAndCreatedUserId(String departmentCode, Integer createdUserId);

    Optional<Department> findByDepartmentCodeAndCreatedUserId(String departmentCode, Integer createdUserId);

    Optional<Department> findByIdAndCreatedUserId(Integer id, Integer createdUserId);

    Page<Department> findByCreatedUserId(Integer createdUserId, Pageable pageable);
}
