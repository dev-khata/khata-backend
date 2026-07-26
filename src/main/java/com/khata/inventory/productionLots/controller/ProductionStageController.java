package com.khata.inventory.productionLots.controller;

import com.khata.inventory.productionLots.dto.ProductionStageDTO;
import com.khata.inventory.productionLots.dto.ProductionStageDepartmentMappingDTO;
import com.khata.inventory.productionLots.dto.ProductionStageWorkTypeOptionDTO;
import com.khata.inventory.productionLots.service.ProductionStageService;
import com.khata.payload.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/production-stages")
@RequiredArgsConstructor
public class ProductionStageController {

    private final ProductionStageService productionStageService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductionStageDTO>>> getProductionStages() {
        List<ProductionStageDTO> stages = productionStageService.getProductionStages();
        return ResponseEntity.ok(new ApiResponse<>(stages, HttpStatus.OK.value()));
    }

    @GetMapping("/work-types")
    public ResponseEntity<List<ProductionStageWorkTypeOptionDTO>> getProductionStageWorkTypes() {
        return ResponseEntity.ok(productionStageService.getProductionStageWorkTypes());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse<ProductionStageDTO>> createProductionStage(
            @Valid @RequestBody ProductionStageDTO productionStageDTO) {
        ProductionStageDTO productionStage = productionStageService.createProductionStage(productionStageDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(
                        productionStage,
                        HttpStatus.CREATED.value(),
                        "Production stage created successfully"));
    }

    @PutMapping("/{stageId}")
    public ResponseEntity<ApiResponse<ProductionStageDTO>> updateProductionStage(
            @PathVariable Integer stageId,
            @Valid @RequestBody ProductionStageDTO productionStageDTO) {
        ProductionStageDTO productionStage = productionStageService.updateProductionStage(stageId, productionStageDTO);
        return ResponseEntity.ok(new ApiResponse<>(
                productionStage,
                HttpStatus.OK.value(),
                "Production stage updated successfully"));
    }

    @DeleteMapping("/{stageId}")
    public ResponseEntity<ApiResponse<Void>> deleteProductionStage(@PathVariable Integer stageId) {
        productionStageService.deleteProductionStage(stageId);
        return ResponseEntity.ok(new ApiResponse<>(
                null,
                HttpStatus.OK.value(),
                "Production stage deleted successfully"));
    }

    @PutMapping("/{stageId}/departments")
    public ResponseEntity<ApiResponse<ProductionStageDTO>> updateDepartments(
            @PathVariable Integer stageId,
            @Valid @RequestBody List<@Valid ProductionStageDepartmentMappingDTO> departmentDTOs) {
        ProductionStageDTO productionStage = productionStageService.updateDepartments(stageId, departmentDTOs);
        return ResponseEntity.ok(new ApiResponse<>(
                productionStage,
                HttpStatus.OK.value(),
                "Production stage departments updated successfully"));
    }
}
