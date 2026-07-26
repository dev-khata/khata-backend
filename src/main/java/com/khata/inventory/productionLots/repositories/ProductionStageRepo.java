package com.khata.inventory.productionLots.repositories;

import com.khata.inventory.productionLots.entity.ProductionStage;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductionStageRepo extends JpaRepository<ProductionStage, Integer> {

    Optional<ProductionStage> findByStageCodeAndCreatedUserId(String stageCode, Integer createdUserId);

    Optional<ProductionStage> findByDisplayOrderAndCreatedUserId(Integer displayOrder, Integer createdUserId);

    @EntityGraph(attributePaths = {"departmentMappings", "departmentMappings.department"})
    Optional<ProductionStage> findByIdAndCreatedUserId(Integer id, Integer createdUserId);

    @EntityGraph(attributePaths = {"departmentMappings", "departmentMappings.department"})
    List<ProductionStage> findByCreatedUserIdOrderByDisplayOrderAscIdAsc(Integer createdUserId);
}
