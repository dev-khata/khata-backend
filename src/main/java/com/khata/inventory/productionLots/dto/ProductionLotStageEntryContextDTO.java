package com.khata.inventory.productionLots.dto;

import com.khata.inventory.productionLots.entity.enums.ProductionStageWorkType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ProductionLotStageEntryContextDTO {

    private Integer productionLotId;

    private Integer productId;

    private Integer currentStageId;

    private String currentStageName;

    private ProductionStageWorkType currentWorkType;

    private Integer expectedPieces;

    private Integer previousStageId;

    private String previousStageName;

    private Integer previousCompletedPieces;

    private Boolean previousStageCompleted;

    private ProductionLotStageEntryDTO entry;
}
