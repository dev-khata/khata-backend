package com.khata.settings.chartOfAccount.controller;

import com.khata.settings.chartOfAccount.dto.ChartOfAccountDTO;
import com.khata.settings.chartOfAccount.services.ChartOfAccountServices;
import com.khata.payload.ApiResponse;
import com.khata.payload.PaginationResponse;
import com.khata.utils.PaginationUtil;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chart-of-account")
@AllArgsConstructor
public class CharOfAccountController {

    private final ChartOfAccountServices chartOfAccountServices;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse<ChartOfAccountDTO>> createChartOfAccount(@Valid @RequestBody ChartOfAccountDTO chartOfAccountDTO){
        ChartOfAccountDTO chartOfAccount = chartOfAccountServices.createChartOfAccount(chartOfAccountDTO);
        ApiResponse<ChartOfAccountDTO> response = new ApiResponse<>(
                chartOfAccount,HttpStatus.CREATED.value(), "Chart of account created successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PaginationResponse<ChartOfAccountDTO>>> getChartOfAccounts(Pageable pageable){
        Page<ChartOfAccountDTO> chartOfAccountDTOPage = chartOfAccountServices.getChartOfAccounts(pageable);
        PaginationResponse<ChartOfAccountDTO> paginationPayload = PaginationUtil.buildPaginationResponse(chartOfAccountDTOPage);
        ApiResponse<PaginationResponse<ChartOfAccountDTO>> response = new ApiResponse<>(paginationPayload, HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{chartOfAccountId}")
    public ResponseEntity<ApiResponse<ChartOfAccountDTO>> updateChartOfAccount(
            @Valid @RequestBody ChartOfAccountDTO chartOfAccountDTO,
            @PathVariable Integer chartOfAccountId){
        ChartOfAccountDTO chartOfAccount = chartOfAccountServices.updateChartOfAccount(chartOfAccountDTO, chartOfAccountId);
        ApiResponse<ChartOfAccountDTO> response = new ApiResponse<>(
                chartOfAccount, HttpStatus.OK.value(), "Chart of account updated successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{chartOfAccountId}")
    public ResponseEntity<ApiResponse<ChartOfAccountDTO>> getChartOfAccountDetails(
            @PathVariable Integer chartOfAccountId){
        ChartOfAccountDTO chartOfAccountDTO = chartOfAccountServices.getChartOfAccountById(chartOfAccountId);
        return ResponseEntity.ok(new ApiResponse<>(chartOfAccountDTO,HttpStatus.OK.value()));
    }

    @DeleteMapping("/{chartOfAccountId}")
    public ResponseEntity<ApiResponse<Void>> deleteChartOfAccountById(
            @PathVariable Integer chartOfAccountId){
        chartOfAccountServices.deleteChartOfAccount(chartOfAccountId);
        ApiResponse<Void> response = new ApiResponse<>(null, HttpStatus.OK.value(), "Chart of account deleted successfully");
        return ResponseEntity.ok(response);
    }


}
