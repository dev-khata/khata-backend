package com.khata.staff.employee.controller;

import com.khata.payload.ApiResponse;
import com.khata.payload.PaginationResponse;
import com.khata.staff.employee.dto.EmployeeDTO;
import com.khata.staff.employee.service.EmployeeService;
import com.khata.utils.PaginationUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/staff/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private static final String EXCEL_MEDIA_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    private final EmployeeService employeeService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse<EmployeeDTO>> createEmployee(@Valid @RequestBody EmployeeDTO employeeDTO) {
        EmployeeDTO employee = employeeService.createEmployee(employeeDTO);
        ApiResponse<EmployeeDTO> response = new ApiResponse<>(
                employee, HttpStatus.CREATED.value(), "Employee created successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PaginationResponse<EmployeeDTO>>> getEmployees(Pageable pageable) {
        Page<EmployeeDTO> employeePage = employeeService.getEmployees(pageable);
        PaginationResponse<EmployeeDTO> paginationPayload = PaginationUtil.buildPaginationResponse(employeePage);
        return ResponseEntity.ok(new ApiResponse<>(paginationPayload, HttpStatus.OK.value()));
    }

    @GetMapping("/{employeeId}")
    public ResponseEntity<ApiResponse<EmployeeDTO>> getEmployeeById(@PathVariable Integer employeeId) {
        EmployeeDTO employee = employeeService.getEmployeeById(employeeId);
        return ResponseEntity.ok(new ApiResponse<>(employee, HttpStatus.OK.value()));
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportEmployees() {
        byte[] excelBytes = employeeService.exportEmployees();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"employees.xlsx\"")
                .contentType(MediaType.parseMediaType(EXCEL_MEDIA_TYPE))
                .body(excelBytes);
    }

    @PutMapping("/{employeeId}")
    public ResponseEntity<ApiResponse<EmployeeDTO>> updateEmployee(
            @PathVariable Integer employeeId,
            @Valid @RequestBody EmployeeDTO employeeDTO) {
        EmployeeDTO employee = employeeService.updateEmployee(employeeId, employeeDTO);
        ApiResponse<EmployeeDTO> response = new ApiResponse<>(
                employee, HttpStatus.OK.value(), "Employee updated successfully");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{employeeId}")
    public ResponseEntity<ApiResponse<Void>> deleteEmployee(@PathVariable Integer employeeId) {
        employeeService.deleteEmployee(employeeId);
        return ResponseEntity.ok(new ApiResponse<>(null, HttpStatus.OK.value(), "Employee deleted successfully"));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PaginationResponse<EmployeeDTO>>> searchEmployeeByName(
            @RequestParam String keyword,
            Pageable pageable) {
        Page<EmployeeDTO> employeePage = employeeService.searchEmployeeByName(keyword, pageable);
        PaginationResponse<EmployeeDTO> paginationPayload = PaginationUtil.buildPaginationResponse(employeePage);
        return ResponseEntity.ok(new ApiResponse<>(paginationPayload, HttpStatus.OK.value()));
    }
}
