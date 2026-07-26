package com.khata.inventory.productionLots.repositories;

import com.khata.inventory.productionLots.entity.ProductionStageDepartmentMapping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductionStageDepartmentMappingRepo
        extends JpaRepository<ProductionStageDepartmentMapping, Integer> {

    boolean existsByDepartmentIdAndCreatedUserId(Integer departmentId, Integer createdUserId);
}
