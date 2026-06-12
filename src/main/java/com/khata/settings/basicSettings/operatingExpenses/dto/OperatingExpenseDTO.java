package com.khata.settings.basicSettings.operatingExpenses.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor
@Getter
@Setter
public class OperatingExpenseDTO {

    private Integer id;

    @NotBlank(message = "Expense name cannot be blank.")
    @Size(max = 100, message = "Expense name must be less than 100 characters.")
    private String expenseName;

    @NotNull(message = "Amount per piece cannot be null.")
    @DecimalMin(value = "0.0", inclusive = true, message = "Amount per piece must be greater than or equal to 0.")
    private BigDecimal amountPerPiece;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer createdUserId;
}
