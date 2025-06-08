package com.khata.accountType.util;

import com.khata.accountType.entity.enums.TransactionType;

public enum SystemAccountTypes {

    ASSET("Asset", TransactionType.DEBIT, "Resources owned by the business"),
    EXPENSE("Expense", TransactionType.DEBIT, "Costs incurred to generate revenue"),
    LIABILITY("Liability", TransactionType.CREDIT, "Obligations owed by the business"),
    EQUITY("Equity", TransactionType.CREDIT, "Owner’s equity or capital"),
    INCOME("Income", TransactionType.CREDIT, "Revenue earned by the business");

    private final String name;
    private final TransactionType transactionType;
    private final String description;

    SystemAccountTypes(String name, TransactionType transactionType, String description) {
        this.name = name;
        this.transactionType = transactionType;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public String getDescription() {
        return description;
    }
}
