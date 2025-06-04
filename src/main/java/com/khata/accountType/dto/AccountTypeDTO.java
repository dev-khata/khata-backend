package com.khata.accountType.dto;

import com.khata.accountType.entity.enums.AccountCategoryType;
import com.khata.accountType.entity.enums.TransactionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class AccountTypeDTO {

    @NotNull(message = "Account type cannot be null.")
    private AccountCategoryType name;

    @NotNull(message = "Transaction type cannot be null.")
    private TransactionType transactionType;

    private boolean isDefault;

    @Size(max = 300, message = "Description must be less than 300 characters.")
    private String description;
}
