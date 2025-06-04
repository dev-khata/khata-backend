package com.khata.accountType.repositories;

import com.khata.accountType.entity.AccountType;
import com.khata.accountType.entity.enums.AccountCategoryType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountTypeRepo extends JpaRepository<AccountType, Integer> {
    boolean existsByName(AccountCategoryType name);
}
