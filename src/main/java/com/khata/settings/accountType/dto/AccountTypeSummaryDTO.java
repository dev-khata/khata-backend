package com.khata.settings.accountType.dto;

import com.khata.settings.accountType.entity.enums.TransactionType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class AccountTypeSummaryDTO {
    @NotNull(message = "Account type id cannot be null.")
    private Integer id;

    private String name;

    private TransactionType transactionType;
}
