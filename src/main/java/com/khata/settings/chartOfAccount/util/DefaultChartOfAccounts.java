package com.khata.settings.chartOfAccount.util;

import com.khata.settings.accountType.util.DefaultAccountTypes;

public enum DefaultChartOfAccounts {

    DEBTORS_ACCOUNT(
            "Debtors Account",
            DefaultAccountTypes.ASSET,
            "Amounts owed to the business by customers for goods or services sold on credit. Represents future economic benefits. Resources owned by the business."
    ),
    CREDITORS_ACCOUNT(
            "Creditors Account",
            DefaultAccountTypes.LIABILITY,
            "Obligations of the business to pay suppliers for goods or services received on credit. Represents amounts payable in the future."
    ),
    BANK_ACCOUNT(
            "Bank Account",
            DefaultAccountTypes.ASSET,
            "Represents funds held in the organization’s bank accounts. Used for recording deposits, withdrawals, and bank-related transactions."
    );

    private final String name;
    private final DefaultAccountTypes systemAccountTypes;
    private final String description;

    DefaultChartOfAccounts(String name, DefaultAccountTypes systemAccountTypes, String description) {
        this.name = name;
        this.systemAccountTypes = systemAccountTypes;
        this.description = description;
    }

    public String getName(){
        return name;
    }

    public DefaultAccountTypes getDefaultAccountType(){
        return systemAccountTypes;
    }

    public String getDescription(){
        return description;
    }
}
