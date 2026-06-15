package com.khata.inventory.rawMaterial.controller;

import com.khata.payload.ApiResponse;
import com.khata.payload.PaginationResponse;
import com.khata.inventory.rawMaterial.dto.RawMaterialStockBatchDTO;
import com.khata.inventory.rawMaterial.service.RawMaterialStockBatchService;
import com.khata.utils.PaginationUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/raw-material/{materialId}/stock-batch")
@Tag(name = "RawMaterialStockBatch")
@AllArgsConstructor
public class RawMaterialStockBatchController {

    private final RawMaterialStockBatchService stockBatchService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse<RawMaterialStockBatchDTO>> createRawMaterialStockBatch(
            @PathVariable Integer materialId,
            @Valid @RequestBody RawMaterialStockBatchDTO stockBatchDTO) {
        RawMaterialStockBatchDTO stockBatch = stockBatchService.createRawMaterialStockBatch(materialId, stockBatchDTO);
        ApiResponse<RawMaterialStockBatchDTO> response = new ApiResponse<>(stockBatch, HttpStatus.CREATED.value(), "Raw material stock batch created successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PaginationResponse<RawMaterialStockBatchDTO>>> getRawMaterialStockBatchesByRawMaterialId(
            @PathVariable Integer materialId,
            @RequestParam(required = false) Integer fiscalYearId,
            Pageable pageable) {
        Page<RawMaterialStockBatchDTO> stockBatchPage = stockBatchService.getRawMaterialStockBatchesByRawMaterialId(
                materialId, fiscalYearId, pageable);
        PaginationResponse<RawMaterialStockBatchDTO> paginationPayload = PaginationUtil.buildPaginationResponse(stockBatchPage);
        return ResponseEntity.ok(new ApiResponse<>(paginationPayload, HttpStatus.OK.value()));
    }

    @PutMapping("/{stockBatchId}")
    public ResponseEntity<ApiResponse<RawMaterialStockBatchDTO>> updateRawMaterialStockBatch(
            @PathVariable Integer materialId,
            @Valid @RequestBody RawMaterialStockBatchDTO stockBatchDTO,
            @PathVariable Integer stockBatchId) {
        RawMaterialStockBatchDTO stockBatch = stockBatchService.updateRawMaterialStockBatch(materialId, stockBatchDTO, stockBatchId);
        ApiResponse<RawMaterialStockBatchDTO> response = new ApiResponse<>(stockBatch, HttpStatus.OK.value(), "Raw material stock batch updated successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{stockBatchId}")
    public ResponseEntity<ApiResponse<RawMaterialStockBatchDTO>> getRawMaterialStockBatchDetails(
            @PathVariable Integer materialId,
            @PathVariable Integer stockBatchId) {
        RawMaterialStockBatchDTO stockBatch = stockBatchService.getRawMaterialStockBatchById(materialId, stockBatchId);
        return ResponseEntity.ok(new ApiResponse<>(stockBatch, HttpStatus.OK.value()));
    }

    @DeleteMapping("/{stockBatchId}")
    public ResponseEntity<ApiResponse<Void>> deleteRawMaterialStockBatch(
            @PathVariable Integer materialId,
            @PathVariable Integer stockBatchId) {
        stockBatchService.deleteRawMaterialStockBatch(materialId, stockBatchId);
        return ResponseEntity.ok(new ApiResponse<>(null, HttpStatus.OK.value(), "Raw material stock batch deleted successfully"));
    }
}
