package com.khata.inventory.productionLots.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ProductionLotStageEntryEmployeeDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer id;

    private Integer departmentId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String departmentName;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String departmentCode;

    @NotNull(message = "Employee cannot be null.")
    private Integer employeeId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String employeeName;

    @Size(max = 50, message = "Role code must be less than 50 characters.")
    private String roleCode;

    @Size(max = 100, message = "Role name must be less than 100 characters.")
    private String roleName;

    @Min(value = 1, message = "Pieces must be greater than 0.")
    private Integer pieces;
}
