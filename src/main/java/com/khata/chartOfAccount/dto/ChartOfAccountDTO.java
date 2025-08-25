package com.khata.chartOfAccount.dto;

import com.khata.accountType.dto.AccountTypeSummaryDTO;
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

    private AccountTypeSummaryDTO accountType;

    private boolean isSystemDefault;

    private boolean isActive;
}
