package com.khata.inventory.productionLots.controller;

import com.khata.inventory.productionLots.dto.ProductionLotStageEntryDTO;
import com.khata.inventory.productionLots.dto.ProductionLotStageEntryContextDTO;
import com.khata.inventory.productionLots.entity.enums.ProductionStageWorkType;
import com.khata.inventory.productionLots.service.ProductionLotStageEntryService;
import com.khata.payload.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/production-lots/{productionLotId}/stage-entries")
@RequiredArgsConstructor
public class ProductionLotStageEntryController {

    private final ProductionLotStageEntryService stageEntryService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductionLotStageEntryDTO>> saveOrUpdateEntry(
            @PathVariable Integer productionLotId,
            @Valid @RequestBody ProductionLotStageEntryDTO entryDTO) {
        ProductionLotStageEntryDTO stageEntry = stageEntryService.saveOrUpdateEntry(productionLotId, entryDTO);
        return ResponseEntity.ok(new ApiResponse<>(
                stageEntry,
                HttpStatus.OK.value(),
                "Production lot stage entry saved successfully"));
    }

    @GetMapping("/context")
    public ResponseEntity<ApiResponse<ProductionLotStageEntryContextDTO>> getContext(
            @PathVariable Integer productionLotId,
            @RequestParam Integer productId,
            @RequestParam Integer productionStageId,
            @RequestParam(required = false) Integer departmentId) {
        ProductionLotStageEntryContextDTO context = stageEntryService.getContext(
                productionLotId,
                productId,
                productionStageId,
                departmentId);
        return ResponseEntity.ok(new ApiResponse<>(context, HttpStatus.OK.value()));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<ProductionLotStageEntryDTO>> getEntry(
            @PathVariable Integer productionLotId,
            @RequestParam Integer productId,
            @RequestParam Integer productionStageId,
            @RequestParam(required = false) Integer departmentId,
            @RequestParam(required = false) ProductionStageWorkType workType) {
        ProductionLotStageEntryDTO stageEntry = stageEntryService.getEntry(
                productionLotId,
                productId,
                productionStageId,
                departmentId,
                workType);
        return ResponseEntity.ok(new ApiResponse<>(stageEntry, HttpStatus.OK.value()));
    }

    @PostMapping("/{entryId}/complete")
    public ResponseEntity<ApiResponse<ProductionLotStageEntryDTO>> completeEntry(
            @PathVariable Integer productionLotId,
            @PathVariable Integer entryId) {
        ProductionLotStageEntryDTO stageEntry = stageEntryService.completeEntry(productionLotId, entryId);
        return ResponseEntity.ok(new ApiResponse<>(
                stageEntry,
                HttpStatus.OK.value(),
                "Production lot stage entry completed successfully"));
    }
}
