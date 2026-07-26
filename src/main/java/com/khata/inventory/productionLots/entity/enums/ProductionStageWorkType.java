package com.khata.inventory.productionLots.entity.enums;

import lombok.Getter;

@Getter
public enum ProductionStageWorkType {
    CUTTING_PATTERN("Cutting & Pattern"),
    STITCHING("Stitching"),
    GENERIC("Generic"),
    WASHING("Washing"),
    BUTTON_KAAJ("Button Kaaj"),
    FINISHING_ROOM("Finishing Room");

    private final String label;

    ProductionStageWorkType(String label) {
        this.label = label;
    }
}
