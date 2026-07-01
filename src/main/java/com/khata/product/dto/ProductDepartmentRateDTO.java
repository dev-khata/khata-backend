package com.khata.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor
@Getter
@Setter
public class ProductDepartmentRateDTO {

    private Integer id;

    @NotNull(message = "Department cannot be null")
    private Integer departmentId;

    private String departmentCode;

    private String departmentName;

    @NotNull(message = "Rate cannot be null")
    @DecimalMin(value = "0.0", message = "Rate must be greater than or equal to 0")
    private BigDecimal rate;
}
