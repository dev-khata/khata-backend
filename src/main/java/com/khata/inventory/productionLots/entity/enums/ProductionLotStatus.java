package com.khata.inventory.productionLots.entity.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ProductionLotStatus {
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled");

    private final String label;

    ProductionLotStatus(String label) {
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }
}
