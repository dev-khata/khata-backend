package com.khata.chartOfAccount.util;

import com.khata.accountType.entity.AccountType;
import com.khata.accountType.repositories.AccountTypeRepo;
import com.khata.chartOfAccount.entity.ChartOfAccount;
import com.khata.chartOfAccount.repositories.ChartOfAccountRepo;
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
                chartOfAccountRepo.findByName(preDefinedChartOfAccounts.getName()).orElseGet(() -> {
                    ChartOfAccount chartOfAccount = new ChartOfAccount();
                    chartOfAccount.setName(preDefinedChartOfAccounts.getName());
                    chartOfAccount.setDescription(preDefinedChartOfAccounts.getDescription());

                    // Lookup AccountType entity by enum name
                    AccountType accountType = accountTypeRepo.findByName(
                            preDefinedChartOfAccounts.getDefaultAccountType().getName()
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
