package com.khata.settings.department.services.impl;

import com.khata.exceptions.ResourceAlreadyExistsException;
import com.khata.exceptions.ResourceNotFoundException;
import com.khata.settings.department.dto.DepartmentDTO;
import com.khata.settings.department.entity.Department;
import com.khata.settings.department.repositories.DepartmentRepo;
import com.khata.settings.department.services.DepartmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepo departmentRepo;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public DepartmentDTO createDepartment(DepartmentDTO departmentDTO) {
        if (departmentRepo.existsByDepartmentCode(departmentDTO.getDepartmentCode())) {
            alreadyExists(departmentDTO.getDepartmentCode());
        }

        Department department = modelMapper.map(departmentDTO, Department.class);
        Department savedDepartment = departmentRepo.save(department);
        log.info("Department created with code : {}", savedDepartment.getDepartmentCode());
        return modelMapper.map(savedDepartment, DepartmentDTO.class);
    }

    @Override
    @Transactional
    public DepartmentDTO updateDepartment(DepartmentDTO departmentDTO, Integer departmentId) {
        Department department = getDepartmentEntityById(departmentId);

        Optional<Department> existingByCode = departmentRepo.findByDepartmentCode(departmentDTO.getDepartmentCode());
        if (existingByCode.isPresent() && !existingByCode.get().getId().equals(departmentId)) {
            alreadyExists(departmentDTO.getDepartmentCode());
        }

        department.setDepartmentCode(departmentDTO.getDepartmentCode());
        department.setDepartmentName(departmentDTO.getDepartmentName());

        Department updatedDepartment = departmentRepo.save(department);
        log.info("Department updated with id : {}", departmentId);
        return modelMapper.map(updatedDepartment, DepartmentDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentDTO getDepartmentById(Integer departmentId) {
        Department department = getDepartmentEntityById(departmentId);
        return modelMapper.map(department, DepartmentDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DepartmentDTO> getDepartments(Pageable pageable) {
        Page<Department> departments = departmentRepo.findAll(pageable);
        return departments.map(department -> modelMapper.map(department, DepartmentDTO.class));
    }

    @Override
    @Transactional
    public void deleteDepartment(Integer departmentId) {
        Department department = getDepartmentEntityById(departmentId);
        departmentRepo.delete(department);
        log.info("Department deleted with id : {}", departmentId);
    }

    private Department getDepartmentEntityById(Integer departmentId) {
        return departmentRepo.findById(departmentId).orElseThrow(
                () -> new ResourceNotFoundException("Department with", "id", departmentId));
    }

    private void alreadyExists(String departmentCode) {
        throw new ResourceAlreadyExistsException("Department code", departmentCode);
    }
}
