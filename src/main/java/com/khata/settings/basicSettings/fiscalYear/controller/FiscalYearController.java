package com.khata.settings.basicSettings.fiscalYear.controller;

import com.khata.payload.ApiResponse;
import com.khata.payload.PaginationResponse;
import com.khata.settings.basicSettings.fiscalYear.dto.FiscalYearDTO;
import com.khata.settings.basicSettings.fiscalYear.services.FiscalYearService;
import com.khata.utils.PaginationUtil;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/fiscal-year")
@AllArgsConstructor
public class FiscalYearController {

    private final FiscalYearService fiscalYearService;

    @GetMapping("/current")
    public ResponseEntity<ApiResponse<FiscalYearDTO>> getCurrentFiscalYear() {
        FiscalYearDTO fiscalYear = fiscalYearService.getCurrentFiscalYear();
        ApiResponse<FiscalYearDTO> response = new ApiResponse<>(fiscalYear, HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PaginationResponse<FiscalYearDTO>>> getFiscalYears(Pageable pageable) {
        Page<FiscalYearDTO> fiscalYearDTOPage = fiscalYearService.getFiscalYears(pageable);
        PaginationResponse<FiscalYearDTO> paginationPayload = PaginationUtil.buildPaginationResponse(fiscalYearDTOPage);
        ApiResponse<PaginationResponse<FiscalYearDTO>> response = new ApiResponse<>(paginationPayload, HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }
}
