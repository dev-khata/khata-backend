package com.khata.settings.accountType.util;

import com.khata.settings.accountType.entity.AccountType;
import com.khata.settings.accountType.repositories.AccountTypeRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
public class AccountTypeInitializer {

    @Bean
    @Order(1)
    CommandLineRunner initAccountTypes(AccountTypeRepo accountTypeRepo){
        return args -> {
            for (DefaultAccountTypes predefinedAccountType : DefaultAccountTypes.values()){
                accountTypeRepo.findByName(predefinedAccountType.getName()).orElseGet(()-> {
                    AccountType type = new AccountType();
                    type.setName(predefinedAccountType.getName());
                    type.setDescription(predefinedAccountType.getDescription());
                    type.setTransactionType(predefinedAccountType.getTransactionType());
                    type.setSystemDefault(true);
                    return accountTypeRepo.save(type);
                });
            }
        };
    }
}
