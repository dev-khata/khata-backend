package com.khata.settings.department.controller;

import com.khata.payload.ApiResponse;
import com.khata.payload.PaginationResponse;
import com.khata.settings.department.dto.DepartmentDTO;
import com.khata.settings.department.services.DepartmentService;
import com.khata.utils.PaginationUtil;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/department")
@AllArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse<DepartmentDTO>> createDepartment(@Valid @RequestBody DepartmentDTO departmentDTO) {
        DepartmentDTO department = departmentService.createDepartment(departmentDTO);
        ApiResponse<DepartmentDTO> response = new ApiResponse<>(
                department, HttpStatus.CREATED.value(), "Department created successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PaginationResponse<DepartmentDTO>>> getDepartments(Pageable pageable) {
        Page<DepartmentDTO> departmentDTOPage = departmentService.getDepartments(pageable);
        PaginationResponse<DepartmentDTO> paginationPayload = PaginationUtil.buildPaginationResponse(departmentDTOPage);
        ApiResponse<PaginationResponse<DepartmentDTO>> response = new ApiResponse<>(paginationPayload, HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{departmentId}")
    public ResponseEntity<ApiResponse<DepartmentDTO>> updateDepartment(
            @Valid @RequestBody DepartmentDTO departmentDTO,
            @PathVariable Integer departmentId) {
        DepartmentDTO department = departmentService.updateDepartment(departmentDTO, departmentId);
        ApiResponse<DepartmentDTO> response = new ApiResponse<>(
                department, HttpStatus.OK.value(), "Department updated successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{departmentId}")
    public ResponseEntity<ApiResponse<DepartmentDTO>> getDepartmentDetails(@PathVariable Integer departmentId) {
        DepartmentDTO departmentDTO = departmentService.getDepartmentById(departmentId);
        return ResponseEntity.ok(new ApiResponse<>(departmentDTO, HttpStatus.OK.value()));
    }

    @DeleteMapping("/{departmentId}")
    public ResponseEntity<ApiResponse<Void>> deleteDepartmentById(@PathVariable Integer departmentId) {
        departmentService.deleteDepartment(departmentId);
        ApiResponse<Void> response = new ApiResponse<>(null, HttpStatus.OK.value(), "Department deleted successfully");
        return ResponseEntity.ok(response);
    }
}
