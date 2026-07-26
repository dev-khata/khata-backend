package com.khata.inventory.productionLots.service;

import com.khata.inventory.productionLots.dto.ProductionStageDTO;
import com.khata.inventory.productionLots.dto.ProductionStageDepartmentMappingDTO;
import com.khata.inventory.productionLots.dto.ProductionStageWorkTypeOptionDTO;

import java.util.List;

public interface ProductionStageService {

    List<ProductionStageDTO> getProductionStages();

    List<ProductionStageWorkTypeOptionDTO> getProductionStageWorkTypes();

    ProductionStageDTO createProductionStage(ProductionStageDTO productionStageDTO);

    ProductionStageDTO updateProductionStage(Integer stageId, ProductionStageDTO productionStageDTO);

    void deleteProductionStage(Integer stageId);

    ProductionStageDTO updateDepartments(
            Integer stageId,
            List<ProductionStageDepartmentMappingDTO> departmentDTOs);
}
