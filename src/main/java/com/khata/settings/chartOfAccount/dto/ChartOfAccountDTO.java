package com.khata.settings.chartOfAccount.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.khata.settings.accountType.dto.AccountTypeSummaryDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ChartOfAccountDTO {

    private Integer id;

    @NotNull(message = "Chart of account name cannot be null.")
    private String name;

    @Size(max = 300, message = "Description must be less than 300 characters.")
    private String description;

    @NotNull(message = "Account type cannot be null.")
    @Valid
    private AccountTypeSummaryDTO accountType;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private boolean isSystemDefault;

    private boolean isActive;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer createdUserId;
}
