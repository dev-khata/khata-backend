package com.khata.inventory.rawMaterial.repositories;

import com.khata.inventory.rawMaterial.entity.RawMaterialStockBatch;
import com.khata.inventory.rawMaterial.repositories.projection.RawMaterialRollCountProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface RawMaterialStockBatchRepo extends JpaRepository<RawMaterialStockBatch, Integer> {
    Optional<RawMaterialStockBatch> findByIdAndRawMaterialIdAndCreatedUserId(Integer id, Integer rawMaterialId, Integer createdUserId);

    Page<RawMaterialStockBatch> findByRawMaterialIdAndCreatedUserId(Integer rawMaterialId, Integer createdUserId, Pageable pageable);

    Page<RawMaterialStockBatch> findByRawMaterialIdAndCreatedUserIdAndFiscalYearId(
            Integer rawMaterialId,
            Integer createdUserId,
            Integer fiscalYearId,
            Pageable pageable);

    @Query("select coalesce(sum(batch.rollCount), 0) from RawMaterialStockBatch batch where batch.rawMaterial.id = :rawMaterialId and batch.createdUserId = :createdUserId")
    Long sumRollCountByRawMaterialIdAndCreatedUserId(@Param("rawMaterialId") Integer rawMaterialId, @Param("createdUserId") Integer createdUserId);

    @Query("""
            select batch.rawMaterial.id as rawMaterialId, coalesce(sum(batch.rollCount), 0) as totalRollCount
            from RawMaterialStockBatch batch
            where batch.rawMaterial.id in :rawMaterialIds and batch.createdUserId = :createdUserId
            group by batch.rawMaterial.id
            """)
    List<RawMaterialRollCountProjection> sumRollCountsByRawMaterialIdsAndCreatedUserId(
            @Param("rawMaterialIds") List<Integer> rawMaterialIds,
            @Param("createdUserId") Integer createdUserId);
}
