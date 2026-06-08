package com.khata.settings.accountType.dto;

import com.khata.settings.accountType.entity.enums.TransactionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class AccountTypeDTO {

    private Integer id;

    @NotNull(message = "Account type cannot be null.")
    private String name;

    @NotNull(message = "Transaction type cannot be null.")
    private TransactionType transactionType;

    @NotNull(message = "System default cannot be null.")
    private boolean isSystemDefault;

    @Size(max = 300, message = "Description must be less than 300 characters.")
    private String description;
}
