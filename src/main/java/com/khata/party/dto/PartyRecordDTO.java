package com.khata.party.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.khata.party.entity.enums.TransactionType;
import jakarta.validation.constraints.DecimalMin;
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
public class PartyRecordDTO {
    private Integer id;

    @Size(min = 4, max = 100, message = "Particular must be between 4 and 100 characters.")
    private String particular;

    @NotNull(message = "Amount cannot be null.")
    @DecimalMin(value = "0.0", inclusive = true, message = "Amount must be a positive number.")
    private BigDecimal amount;

    @NotBlank(message = "Nepali date cannot be blank.")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Nepali date must be in yyyy-MM-dd format.")
    private String nepaliDate;

    @NotNull(message = "English date cannot be null.")
    private LocalDate englishDate;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer fiscalYearId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String fiscalYearName;

    @NotNull(message = "Transaction type cannot be null.")
    private TransactionType transactionType;

    private Integer partyId;
}
