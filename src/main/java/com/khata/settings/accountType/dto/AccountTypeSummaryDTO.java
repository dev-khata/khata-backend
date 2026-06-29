package com.khata.settings.accountType.dto;

import com.khata.settings.accountType.entity.enums.TransactionType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class AccountTypeSummaryDTO {
    private Integer id;
    private String name;
    private TransactionType transactionType;
}
