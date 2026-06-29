package com.khata.inventory.productionLots.service;

import com.khata.inventory.productionLots.dto.ProductionLotDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductionLotService {

    ProductionLotDTO createProductionLot(ProductionLotDTO productionLotDTO);

    ProductionLotDTO updateProductionLot(Integer productionLotId, ProductionLotDTO productionLotDTO);

    Page<ProductionLotDTO> getProductionLots(Integer fiscalYearId, Pageable pageable);

    ProductionLotDTO getProductionLotById(Integer productionLotId);

    void deleteProductionLot(Integer productionLotId);
}
