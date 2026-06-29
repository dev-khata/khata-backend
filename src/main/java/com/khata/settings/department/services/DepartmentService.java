package com.khata.settings.department.services;

import com.khata.settings.department.dto.DepartmentDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DepartmentService {
    DepartmentDTO createDepartment(DepartmentDTO departmentDTO);

    DepartmentDTO updateDepartment(DepartmentDTO departmentDTO, Integer departmentId);

    DepartmentDTO getDepartmentById(Integer departmentId);

    Page<DepartmentDTO> getDepartments(Pageable pageable);

    void deleteDepartment(Integer departmentId);
}
