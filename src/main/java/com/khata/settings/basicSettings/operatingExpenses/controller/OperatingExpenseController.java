package com.khata.settings.basicSettings.operatingExpenses.controller;

import com.khata.payload.ApiResponse;
import com.khata.payload.PaginationResponse;
import com.khata.settings.basicSettings.operatingExpenses.dto.OperatingExpenseDTO;
import com.khata.settings.basicSettings.operatingExpenses.services.OperatingExpenseService;
import com.khata.utils.PaginationUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/operating-expenses")
@AllArgsConstructor
public class OperatingExpenseController {

    private final OperatingExpenseService operatingExpenseService;

    @PostMapping("/upsert")
    public ResponseEntity<ApiResponse<List<OperatingExpenseDTO>>> upsertOperatingExpenses(
            @NotEmpty(message = "Operating expenses cannot be empty.")
            @RequestBody List<@Valid OperatingExpenseDTO> operatingExpenseDTOs) {
        List<OperatingExpenseDTO> operatingExpenses = operatingExpenseService.upsertOperatingExpenses(operatingExpenseDTOs);
        ApiResponse<List<OperatingExpenseDTO>> response = new ApiResponse<>(
                operatingExpenses, HttpStatus.OK.value(), "Operating expenses saved successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PaginationResponse<OperatingExpenseDTO>>> getOperatingExpenses(Pageable pageable) {
        Page<OperatingExpenseDTO> operatingExpenseDTOPage = operatingExpenseService.getOperatingExpenses(pageable);
        PaginationResponse<OperatingExpenseDTO> paginationPayload = PaginationUtil.buildPaginationResponse(operatingExpenseDTOPage);
        ApiResponse<PaginationResponse<OperatingExpenseDTO>> response = new ApiResponse<>(paginationPayload, HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{operatingExpenseId}")
    public ResponseEntity<ApiResponse<Void>> deleteOperatingExpenseById(@PathVariable Integer operatingExpenseId) {
        operatingExpenseService.deleteOperatingExpense(operatingExpenseId);
        ApiResponse<Void> response = new ApiResponse<>(null, HttpStatus.OK.value(), "Operating expense deleted successfully");
        return ResponseEntity.ok(response);
    }
}
