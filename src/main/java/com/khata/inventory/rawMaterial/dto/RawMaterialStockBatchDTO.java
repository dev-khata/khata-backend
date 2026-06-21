package com.khata.inventory.rawMaterial.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
public class RawMaterialStockBatchDTO {

    private Integer id;

    @NotBlank(message = "Batch number cannot be blank.")
    @Size(max = 50, message = "Batch number must be less than 50 characters.")
    private String batchNumber;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer rawMaterialId;

    @NotNull(message = "Party cannot be null.")
    private Integer partyId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer fiscalYearId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String fiscalYearName;

    @JsonAlias("purchaseNepaliDate")
    @NotBlank(message = "Nepali purchase date cannot be blank.")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Nepali purchase date must be in yyyy-MM-dd format.")
    private String purchaseDateNepali;

    @JsonAlias("purchaseEnglishDate")
    @NotNull(message = "English purchase date cannot be null.")
    private LocalDate purchaseDateEnglish;

    @JsonAlias("rollCount")
    @NotNull(message = "Total rolls purchased cannot be null.")
    @Min(value = 0, message = "Total rolls purchased must be greater than or equal to 0.")
    private Integer totalRollsPurchased;

    @JsonAlias("totalQuantity")
    @NotNull(message = "Total quantity purchased cannot be null.")
    @DecimalMin(value = "0.0", inclusive = false, message = "Total quantity purchased must be greater than 0.")
    private BigDecimal totalQuantityPurchased;

    @JsonAlias("availableRollCount")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer availableRolls;

    @JsonAlias("remainingQuantity")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private BigDecimal availableQuantity;

    @NotNull(message = "Purchase rate cannot be null.")
    @DecimalMin(value = "0.0", inclusive = true, message = "Purchase rate must be greater than or equal to 0.")
    private BigDecimal purchaseRate;

    private String materialCode;

    private String materialName;

    private String partyName;
}
