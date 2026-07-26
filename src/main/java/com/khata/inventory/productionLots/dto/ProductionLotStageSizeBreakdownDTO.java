package com.khata.inventory.productionLots.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ProductionLotStageSizeBreakdownDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer id;

    @NotBlank(message = "Size is required.")
    @Size(max = 30, message = "Size must be less than 30 characters.")
    private String size;

    @NotNull(message = "Pieces cannot be null.")
    @Min(value = 1, message = "Pieces must be greater than 0.")
    private Integer pieces;
}
