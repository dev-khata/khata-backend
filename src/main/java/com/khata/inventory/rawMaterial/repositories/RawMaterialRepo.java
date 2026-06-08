package com.khata.inventory.rawMaterial.repositories;

import com.khata.inventory.rawMaterial.entity.RawMaterial;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RawMaterialRepo extends JpaRepository<RawMaterial, Integer> {
    Optional<RawMaterial> findByIdAndCreatedUserId(Integer id, Integer createdUserId);

    Optional<RawMaterial> findByMaterialCodeAndCreatedUserId(String materialCode, Integer createdUserId);

    Page<RawMaterial> findByCreatedUserId(Integer createdUserId, Pageable pageable);

    Page<RawMaterial> findByMaterialNameContainingIgnoreCaseAndCreatedUserId(String materialName, Integer createdUserId, Pageable pageable);
}
