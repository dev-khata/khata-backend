package com.khata.inventory.productionLots.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.khata.inventory.rawMaterial.entity.enums.RawMaterialUnit;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class ProductionLotDTO {

    private Integer id;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String productionLotNumber;

    @NotNull(message = "Raw material cannot be null.")
    private Integer materialId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String materialName;

    @NotNull(message = "Issued rolls cannot be null.")
    @Min(value = 1, message = "Issued rolls must be greater than 0.")
    private Integer issuedRolls;

    @NotNull(message = "Issued stock cannot be null.")
    @DecimalMin(value = "0.0", inclusive = false, message = "Issued stock must be greater than 0.")
    private BigDecimal issuedStock;

    @NotNull(message = "Stock unit cannot be null.")
    private RawMaterialUnit stockUnit;

    @JsonAlias({"englishDate", "startDate", "startDateEnglish"})
    @NotNull(message = "Start date cannot be null.")
    private LocalDate startDateInEnglish;

    @JsonAlias({"nepaliDate", "startDateNepali"})
    @NotNull(message = "Nepali date cannot be null.")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Nepali date must be in yyyy-MM-dd format.")
    private String startDateInNepali;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String currentStage;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String status;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDate completeDateInEnglish;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String completeDateInNepali;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer fiscalYearId;

    @NotEmpty(message = "At least one product allocation is required.")
    private List<@Valid ProductionLotAllocationDTO> allocations = new ArrayList<>();
}
