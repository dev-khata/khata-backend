package com.khata.inventory.rawMaterial.service;

import com.khata.inventory.rawMaterial.dto.RawMaterialDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RawMaterialService {
    RawMaterialDTO createRawMaterial(RawMaterialDTO rawMaterialDTO);

    RawMaterialDTO updateRawMaterial(RawMaterialDTO rawMaterialDTO, Integer rawMaterialId);

    RawMaterialDTO getRawMaterialById(Integer rawMaterialId);

    Page<RawMaterialDTO> getRawMaterials(Pageable pageable);

    Page<RawMaterialDTO> searchRawMaterialByName(String name, Pageable pageable);

    void deleteRawMaterial(Integer rawMaterialId);
}
