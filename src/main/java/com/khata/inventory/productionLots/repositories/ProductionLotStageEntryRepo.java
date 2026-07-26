package com.khata.inventory.productionLots.repositories;

import com.khata.inventory.productionLots.entity.ProductionLotStageEntry;
import com.khata.inventory.productionLots.entity.enums.ProductionStageWorkType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductionLotStageEntryRepo extends JpaRepository<ProductionLotStageEntry, Integer> {

    Optional<ProductionLotStageEntry> findByProductionLotIdAndProductIdAndProductionStageIdAndDepartmentIdAndCreatedUserId(
            Integer productionLotId,
            Integer productId,
            Integer productionStageId,
            Integer departmentId,
            Integer createdUserId);

    Optional<ProductionLotStageEntry> findByProductionLotIdAndProductIdAndProductionStageIdAndWorkTypeAndDepartmentIsNullAndCreatedUserId(
            Integer productionLotId,
            Integer productId,
            Integer productionStageId,
            ProductionStageWorkType workType,
            Integer createdUserId);

    Optional<ProductionLotStageEntry> findFirstByProductionLotIdAndProductIdAndWorkTypeAndCreatedUserIdOrderByIdDesc(
            Integer productionLotId,
            Integer productId,
            ProductionStageWorkType workType,
            Integer createdUserId);

    Optional<ProductionLotStageEntry> findByIdAndProductionLotIdAndCreatedUserId(
            Integer id,
            Integer productionLotId,
            Integer createdUserId);

    List<ProductionLotStageEntry> findByProductionLotIdAndProductIdAndProductionStageIdAndCreatedUserIdOrderByIdAsc(
            Integer productionLotId,
            Integer productId,
            Integer productionStageId,
            Integer createdUserId);

    Optional<ProductionLotStageEntry> findFirstByProductionLotIdAndProductIdAndProductionStageIdAndCreatedUserIdOrderByIdDesc(
            Integer productionLotId,
            Integer productId,
            Integer productionStageId,
            Integer createdUserId);
}
