package com.khata.inventory.productionLots.service;

import com.khata.inventory.productionLots.dto.ProductionLotStageEntryDTO;
import com.khata.inventory.productionLots.dto.ProductionLotStageEntryContextDTO;
import com.khata.inventory.productionLots.entity.enums.ProductionStageWorkType;

public interface ProductionLotStageEntryService {

    ProductionLotStageEntryDTO saveOrUpdateEntry(Integer productionLotId, ProductionLotStageEntryDTO entryDTO);

    ProductionLotStageEntryContextDTO getContext(
            Integer productionLotId,
            Integer productId,
            Integer productionStageId,
            Integer departmentId);

    ProductionLotStageEntryDTO getEntry(
            Integer productionLotId,
            Integer productId,
            Integer productionStageId,
            Integer departmentId,
            ProductionStageWorkType workType);

    ProductionLotStageEntryDTO completeEntry(Integer productionLotId, Integer entryId);
}
