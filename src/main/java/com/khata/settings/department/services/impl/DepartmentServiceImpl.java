package com.khata.settings.department.services.impl;

import com.khata.auth.service.UserService;
import com.khata.exceptions.ResourceAlreadyExistsException;
import com.khata.exceptions.ResourceNotFoundException;
import com.khata.settings.department.dto.DepartmentDTO;
import com.khata.settings.department.entity.Department;
import com.khata.settings.department.repositories.DepartmentRepo;
import com.khata.settings.department.services.DepartmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final UserService userService;

    @Override
    @Transactional
    public DepartmentDTO createDepartment(DepartmentDTO departmentDTO) {
        Integer currentUserId = userService.getCurrentUserId();
        if (departmentRepo.existsByDepartmentCodeAndCreatedUserId(departmentDTO.getDepartmentCode(), currentUserId)) {
            alreadyExists(departmentDTO.getDepartmentCode());
        }

        Department department = new Department();
        department.setCreatedUserId(currentUserId);
        setDepartmentFields(department, departmentDTO);
        Department savedDepartment = departmentRepo.save(department);
        log.info("Department created with code : {}", savedDepartment.getDepartmentCode());
        return toDepartmentDTO(savedDepartment);
    }

    @Override
    @Transactional
    public DepartmentDTO updateDepartment(DepartmentDTO departmentDTO, Integer departmentId) {
        Integer currentUserId = userService.getCurrentUserId();
        Department department = getDepartmentEntityById(departmentId, currentUserId);

        Optional<Department> existingByCode = departmentRepo.findByDepartmentCodeAndCreatedUserId(
                departmentDTO.getDepartmentCode(),
                currentUserId);
        if (existingByCode.isPresent() && !existingByCode.get().getId().equals(departmentId)) {
            alreadyExists(departmentDTO.getDepartmentCode());
        }

        department.setDepartmentCode(departmentDTO.getDepartmentCode());
        department.setDepartmentName(departmentDTO.getDepartmentName());
        setPieceRateFields(department, departmentDTO);

        Department updatedDepartment = departmentRepo.save(department);
        log.info("Department updated with id : {}", departmentId);
        return toDepartmentDTO(updatedDepartment);
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentDTO getDepartmentById(Integer departmentId) {
        Integer currentUserId = userService.getCurrentUserId();
        Department department = getDepartmentEntityById(departmentId, currentUserId);
        return toDepartmentDTO(department);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DepartmentDTO> getDepartments(Pageable pageable) {
        Integer currentUserId = userService.getCurrentUserId();
        Page<Department> departments = departmentRepo.findByCreatedUserId(currentUserId, pageable);
        return departments.map(this::toDepartmentDTO);
    }

    @Override
    @Transactional
    public void deleteDepartment(Integer departmentId) {
        Integer currentUserId = userService.getCurrentUserId();
        Department department = getDepartmentEntityById(departmentId, currentUserId);
        departmentRepo.delete(department);
        log.info("Department deleted with id : {}", departmentId);
    }

    private Department getDepartmentEntityById(Integer departmentId, Integer currentUserId) {
        return departmentRepo.findByIdAndCreatedUserId(departmentId, currentUserId).orElseThrow(
                () -> new ResourceNotFoundException("Department with", "id", departmentId));
    }

    private void alreadyExists(String departmentCode) {
        throw new ResourceAlreadyExistsException("Department code", departmentCode);
    }

    private void setDepartmentFields(Department department, DepartmentDTO departmentDTO) {
        department.setDepartmentCode(departmentDTO.getDepartmentCode());
        department.setDepartmentName(departmentDTO.getDepartmentName());
        setPieceRateFields(department, departmentDTO);
    }

    private void setPieceRateFields(Department department, DepartmentDTO departmentDTO) {
        boolean pieceRateEnabled = Boolean.TRUE.equals(departmentDTO.getPieceRateEnabled());
        department.setPieceRateEnabled(pieceRateEnabled);
        department.setDefaultPieceRate(pieceRateEnabled ? departmentDTO.getDefaultPieceRate() : null);
    }

    private DepartmentDTO toDepartmentDTO(Department department) {
        DepartmentDTO departmentDTO = new DepartmentDTO();
        departmentDTO.setId(department.getId());
        departmentDTO.setDepartmentCode(department.getDepartmentCode());
        departmentDTO.setDepartmentName(department.getDepartmentName());
        departmentDTO.setPieceRateEnabled(Boolean.TRUE.equals(department.getPieceRateEnabled()));
        departmentDTO.setDefaultPieceRate(departmentDTO.getPieceRateEnabled() ? department.getDefaultPieceRate() : null);
        departmentDTO.setCreatedUserId(department.getCreatedUserId());
        return departmentDTO;
    }
}
