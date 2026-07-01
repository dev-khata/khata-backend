package com.khata.settings.department.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor
@Getter
@Setter
public class DepartmentDTO {

    private Integer id;

    @NotBlank(message = "Department code cannot be blank.")
    @Size(max = 50, message = "Department code must be less than 50 characters.")
    private String departmentCode;

    @NotBlank(message = "Department name cannot be blank.")
    @Size(max = 100, message = "Department name must be less than 100 characters.")
    private String departmentName;

    private Boolean pieceRateEnabled = false;

    @DecimalMin(value = "0.0", message = "Default piece rate must be greater than or equal to 0.")
    private BigDecimal defaultPieceRate;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer createdUserId;
}
