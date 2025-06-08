package com.khata.product.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor
@Getter
@Setter
public class ProductDTO {

    private Integer id;

    private String productId;

    @NotNull(message = "Quantity cannot be null")
    @Min(value = 0, message = "Quantity must be greater than or equal to 0")
    private Integer quantity;

    @NotNull(message = "Product name cannot be null")
    @Size(max = 100, message = "Product name must be less than 100 characters")
    private String name;

    @NotNull(message = "Purchase price cannot be null")
    @Min(value = 0, message = "Purchase price must be greater than or equal to 0")
    private BigDecimal purchasePrice;

    @NotNull(message = "Selling price cannot be null")
    @Min(value = 0, message = "Selling price must be greater than or equal to 0")
    private BigDecimal sellingPrice;

    @NotNull(message = "Category cannot be null")
    private Integer category;
}
