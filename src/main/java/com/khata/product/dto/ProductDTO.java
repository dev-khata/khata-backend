package com.khata.product.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class ProductDTO {

    private Integer id;

    @NotBlank(message = "Product code cannot be empty")
    @Size(max = 50, message = "Product code must be less than 50 characters")
    private String productCode;

    @NotBlank(message = "Product name cannot be empty")
    @Size(max = 100, message = "Product name must be less than 100 characters")
    private String productName;

    private Integer createdUserId;

    @NotEmpty(message = "Department rates cannot be empty")
    private List<@Valid ProductDepartmentRateDTO> departmentRates;
}
