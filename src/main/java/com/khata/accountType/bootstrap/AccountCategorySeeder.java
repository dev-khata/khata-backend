package com.khata.accountType.bootstrap;

import com.khata.accountType.entity.AccountCategory;
import com.khata.accountType.repositories.AccountCategoryRepository;
import com.khata.accountType.util.AccountCategoryConstants;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class AccountCategorySeeder implements CommandLineRunner {

    @Autowired
    private AccountCategoryRepository accountCategoryRepository;

    @Override
    public void run(String... args) throws Exception {
        for(String name : AccountCategoryConstants.DEFAULT_CATEGORIES){
            accountCategoryRepository.findByName(name).orElseGet(()->{
                AccountCategory category = new AccountCategory();
                category.setName(name);
                category.setSystemDefined(true);
                return accountCategoryRepository.save(category);
            });
        }
    }
}
