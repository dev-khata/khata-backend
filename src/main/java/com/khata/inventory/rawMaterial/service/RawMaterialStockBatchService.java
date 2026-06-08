package com.khata.inventory.rawMaterial.service;

import com.khata.inventory.rawMaterial.dto.RawMaterialStockBatchDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RawMaterialStockBatchService {
    RawMaterialStockBatchDTO createRawMaterialStockBatch(Integer rawMaterialId, RawMaterialStockBatchDTO stockBatchDTO);

    RawMaterialStockBatchDTO updateRawMaterialStockBatch(Integer rawMaterialId, RawMaterialStockBatchDTO stockBatchDTO, Integer stockBatchId);

    RawMaterialStockBatchDTO getRawMaterialStockBatchById(Integer rawMaterialId, Integer stockBatchId);

    Page<RawMaterialStockBatchDTO> getRawMaterialStockBatchesByRawMaterialId(Integer rawMaterialId, Pageable pageable);

    void deleteRawMaterialStockBatch(Integer rawMaterialId, Integer stockBatchId);
}
