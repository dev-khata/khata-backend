package com.khata.inventory.productionLots.repositories;

import com.khata.inventory.productionLots.entity.ProductionLot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProductionLotRepo extends JpaRepository<ProductionLot, Integer> {

    Optional<ProductionLot> findByIdAndCreatedUserId(Integer id, Integer createdUserId);

    Page<ProductionLot> findByCreatedUserIdAndFiscalYearId(Integer createdUserId, Integer fiscalYearId, Pageable pageable);

    @Query("select coalesce(max(productionLot.id), 0) + 1 from ProductionLot productionLot")
    Integer getNextProductionLotSequence();
}
