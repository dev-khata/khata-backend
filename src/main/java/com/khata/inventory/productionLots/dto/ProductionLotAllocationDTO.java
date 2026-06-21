package com.khata.inventory.productionLots.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ProductionLotAllocationDTO {

    @NotNull(message = "Product cannot be null.")
    private Integer productId;

    private String productCode;

    private String productName;
}
