package com.khata.inventory.productionLots.controller;

import com.khata.inventory.productionLots.dto.ProductionLotDTO;
import com.khata.inventory.productionLots.service.ProductionLotService;
import com.khata.payload.ApiResponse;
import com.khata.payload.PaginationResponse;
import com.khata.utils.PaginationUtil;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/production-lots")
@AllArgsConstructor
public class ProductionLotController {

    private final ProductionLotService productionLotService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse<ProductionLotDTO>> createProductionLot(
            @Valid @RequestBody ProductionLotDTO productionLotDTO) {
        ProductionLotDTO productionLot = productionLotService.createProductionLot(productionLotDTO);
        ApiResponse<ProductionLotDTO> response = new ApiResponse<>(
                productionLot, HttpStatus.CREATED.value(), "Production lot created successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{productionLotId}")
    public ResponseEntity<ApiResponse<ProductionLotDTO>> updateProductionLot(
            @PathVariable Integer productionLotId,
            @Valid @RequestBody ProductionLotDTO productionLotDTO) {
        ProductionLotDTO productionLot = productionLotService.updateProductionLot(productionLotId, productionLotDTO);
        ApiResponse<ProductionLotDTO> response = new ApiResponse<>(
                productionLot, HttpStatus.OK.value(), "Production lot updated successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PaginationResponse<ProductionLotDTO>>> getProductionLots(
            @RequestParam(required = false) Integer fiscalYearId,
            Pageable pageable) {
        Page<ProductionLotDTO> productionLotPage = productionLotService.getProductionLots(fiscalYearId, pageable);
        PaginationResponse<ProductionLotDTO> paginationPayload = PaginationUtil.buildPaginationResponse(productionLotPage);
        return ResponseEntity.ok(new ApiResponse<>(paginationPayload, HttpStatus.OK.value()));
    }

    @GetMapping("/{productionLotId}")
    public ResponseEntity<ApiResponse<ProductionLotDTO>> getProductionLotById(@PathVariable Integer productionLotId) {
        ProductionLotDTO productionLot = productionLotService.getProductionLotById(productionLotId);
        return ResponseEntity.ok(new ApiResponse<>(productionLot, HttpStatus.OK.value()));
    }

    @DeleteMapping("/{productionLotId}")
    public ResponseEntity<ApiResponse<Void>> deleteProductionLot(@PathVariable Integer productionLotId) {
        productionLotService.deleteProductionLot(productionLotId);
        return ResponseEntity.ok(new ApiResponse<>(null, HttpStatus.OK.value(), "Production lot deleted successfully"));
    }
}
