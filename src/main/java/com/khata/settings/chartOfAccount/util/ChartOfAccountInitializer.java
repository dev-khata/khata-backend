package com.khata.settings.chartOfAccount.util;

import com.khata.settings.accountType.entity.AccountType;
import com.khata.settings.accountType.repositories.AccountTypeRepo;
import com.khata.settings.chartOfAccount.entity.ChartOfAccount;
import com.khata.settings.chartOfAccount.repositories.ChartOfAccountRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
public class ChartOfAccountInitializer {

    @Bean
    @Order(2)
    CommandLineRunner initChartOfAccounts(ChartOfAccountRepo chartOfAccountRepo, AccountTypeRepo accountTypeRepo){
        return args -> {
            for(DefaultChartOfAccounts preDefinedChartOfAccounts: DefaultChartOfAccounts.values()){
                chartOfAccountRepo.findByNameAndCreatedUserId(
                        preDefinedChartOfAccounts.getName(),
                        null).orElseGet(() -> {
                    ChartOfAccount chartOfAccount = new ChartOfAccount();
                    chartOfAccount.setName(preDefinedChartOfAccounts.getName());
                    chartOfAccount.setDescription(preDefinedChartOfAccounts.getDescription());

                    // Lookup AccountType entity by enum name
                    AccountType accountType = accountTypeRepo.findByNameAndCreatedUserId(
                            preDefinedChartOfAccounts.getDefaultAccountType().getName(),
                            null
                    ).orElseThrow(() -> new RuntimeException("Account type not found"));
                    chartOfAccount.setAccountType(accountType);

                    chartOfAccount.setActive(true);
                    chartOfAccount.setSystemDefault(true);
                    return chartOfAccountRepo.save(chartOfAccount);
                });
            }
        };
    }
}
