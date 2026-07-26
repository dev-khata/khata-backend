package com.khata.inventory.productionLots.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.khata.inventory.productionLots.entity.enums.ProductionStageWorkType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class ProductionLotStageEntryDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer id;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer productionLotId;

    @NotNull(message = "Product cannot be null.")
    private Integer productId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String productName;

    @NotNull(message = "Production stage cannot be null.")
    private Integer productionStageId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String productionStageName;

    private Integer departmentId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String departmentName;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String departmentCode;

    private Integer employeeId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String employeeName;

    @NotNull(message = "Work type cannot be null.")
    private ProductionStageWorkType workType;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer expectedPieces;

    @Min(value = 1, message = "Completed pieces must be greater than 0.")
    private Integer completedPieces;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer extraPieces = 0;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer damagePieces = 0;

    private Boolean addToMaster = false;

    private Boolean deductFromMaster = false;

    @Size(max = 500, message = "Remarks must be less than 500 characters.")
    private String remarks;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Boolean completed = false;

    private List<@Valid ProductionLotStageEntryEmployeeDTO> employees = new ArrayList<>();

    private List<@Valid ProductionLotStageSizeBreakdownDTO> sizeBreakdowns = new ArrayList<>();
}
