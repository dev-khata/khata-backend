package com.khata.settings.department.repositories;

import com.khata.settings.department.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DepartmentRepo extends JpaRepository<Department, Integer> {
    boolean existsByDepartmentCode(String departmentCode);

    Optional<Department> findByDepartmentCode(String departmentCode);
}
