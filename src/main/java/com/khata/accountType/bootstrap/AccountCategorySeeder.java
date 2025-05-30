package com.khata.accountType.bootstrap;

import com.khata.accountType.entity.AccountCategory;
import com.khata.accountType.repositories.AccountCategoryRepository;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@NoArgsConstructor
public class AccountCategorySeeder implements CommandLineRunner {

    @Autowired
    private AccountCategoryRepository accountCategoryRepository;

    private static final List<String> DEFAULT_CATEGORIES = List.of(
            "ASSETS",
            "LIABILITIES",
            "EQUITY",
            "INCOME",
            "EXPENSE");

    @Override
    public void run(String... args) throws Exception {
        for(String name : DEFAULT_CATEGORIES){
            accountCategoryRepository.findByName(name).orElseGet(()->{
                AccountCategory category = new AccountCategory();
                category.setName(name);
                category.setSystemDefined(true);
                return accountCategoryRepository.save(category);
            });
        }
    }
}
