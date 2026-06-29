package com.khata.inventory.rawMaterial.controller;

import com.khata.payload.ApiResponse;
import com.khata.payload.PaginationResponse;
import com.khata.inventory.rawMaterial.dto.RawMaterialDTO;
import com.khata.inventory.rawMaterial.service.RawMaterialService;
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
@RequestMapping("/api/raw-material")
@Tag(name = "RawMaterial")
@AllArgsConstructor
public class RawMaterialController {

    private final RawMaterialService rawMaterialService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse<RawMaterialDTO>> createRawMaterial(@Valid @RequestBody RawMaterialDTO rawMaterialDTO) {
        RawMaterialDTO rawMaterial = rawMaterialService.createRawMaterial(rawMaterialDTO);
        ApiResponse<RawMaterialDTO> response = new ApiResponse<>(rawMaterial, HttpStatus.CREATED.value(), "Raw material created successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PaginationResponse<RawMaterialDTO>>> getRawMaterials(Pageable pageable) {
        Page<RawMaterialDTO> rawMaterialPage = rawMaterialService.getRawMaterials(pageable);
        PaginationResponse<RawMaterialDTO> paginationPayload = PaginationUtil.buildPaginationResponse(rawMaterialPage);
        return ResponseEntity.ok(new ApiResponse<>(paginationPayload, HttpStatus.OK.value()));
    }

    @PutMapping("/{materialId}")
    public ResponseEntity<ApiResponse<RawMaterialDTO>> updateRawMaterial(
            @Valid @RequestBody RawMaterialDTO rawMaterialDTO,
            @PathVariable Integer materialId) {
        RawMaterialDTO rawMaterial = rawMaterialService.updateRawMaterial(rawMaterialDTO, materialId);
        ApiResponse<RawMaterialDTO> response = new ApiResponse<>(rawMaterial, HttpStatus.OK.value(), "Raw material updated successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{materialId}")
    public ResponseEntity<ApiResponse<RawMaterialDTO>> getRawMaterialDetails(@PathVariable Integer materialId) {
        RawMaterialDTO rawMaterial = rawMaterialService.getRawMaterialById(materialId);
        return ResponseEntity.ok(new ApiResponse<>(rawMaterial, HttpStatus.OK.value()));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PaginationResponse<RawMaterialDTO>>> searchRawMaterialByName(@RequestParam String keyword, Pageable pageable) {
        Page<RawMaterialDTO> rawMaterialPage = rawMaterialService.searchRawMaterialByName(keyword, pageable);
        PaginationResponse<RawMaterialDTO> paginationPayload = PaginationUtil.buildPaginationResponse(rawMaterialPage);
        return ResponseEntity.ok(new ApiResponse<>(paginationPayload, HttpStatus.OK.value()));
    }

    @DeleteMapping("/{materialId}")
    public ResponseEntity<ApiResponse<Void>> deleteRawMaterial(@PathVariable Integer materialId) {
        rawMaterialService.deleteRawMaterial(materialId);
        return ResponseEntity.ok(new ApiResponse<>(null, HttpStatus.OK.value(), "Raw material deleted successfully"));
    }
}
