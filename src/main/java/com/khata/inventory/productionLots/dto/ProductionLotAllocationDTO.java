package com.khata.inventory.productionLots.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
public class ProductionLotAllocationDTO {

    @NotNull(message = "Product cannot be null.")
    private Integer productId;

    private String productCode;

    private String productName;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String status;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer currentStageId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String currentStageName;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDate completeDateInEnglish;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String completeDateInNepali;
}
