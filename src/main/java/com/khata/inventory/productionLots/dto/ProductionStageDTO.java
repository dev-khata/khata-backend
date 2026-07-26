package com.khata.inventory.productionLots.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.khata.inventory.productionLots.entity.enums.ProductionStageWorkType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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
public class ProductionStageDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer id;

    @Size(max = 50, message = "Stage code must be less than 50 characters.")
    private String stageCode;

    @NotBlank(message = "Stage name cannot be blank.")
    @Size(max = 100, message = "Stage name must be less than 100 characters.")
    private String stageName;

    @NotNull(message = "Display order cannot be null.")
    @Min(value = 1, message = "Display order must be positive.")
    private Integer displayOrder;

    @NotNull(message = "Work type cannot be null.")
    private ProductionStageWorkType workType;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotEmpty(message = "At least one department is required.")
    private List<@NotNull(message = "Department cannot be null.") Integer> departmentIds = new ArrayList<>();

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<@Valid ProductionStageDepartmentMappingDTO> departments = new ArrayList<>();
}
