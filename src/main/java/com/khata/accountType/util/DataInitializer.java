package com.khata.accountType.util;

import com.khata.accountType.entity.AccountType;
import com.khata.accountType.repositories.AccountTypeRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initAccountTypes(AccountTypeRepo accountTypeRepo){
        return args -> {
            for (SystemAccountTypes predefinedAccountType : SystemAccountTypes.values()){
                accountTypeRepo.findByName(predefinedAccountType.getName()).orElseGet(()-> {
                    AccountType type = new AccountType();
                    type.setName(predefinedAccountType.getName());
                    type.setDescription(predefinedAccountType.getDescription());
                    type.setTransactionType(predefinedAccountType.getTransactionType());
                    type.setSystemDefined(true);
                    return accountTypeRepo.save(type);
                });
            }
        };
    }
}
