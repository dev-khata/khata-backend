package com.khata.inventory.productionLots.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ProductionStageDepartmentMappingDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer id;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull(message = "Department cannot be null.")
    private Integer departmentId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String departmentName;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String departmentCode;
}
