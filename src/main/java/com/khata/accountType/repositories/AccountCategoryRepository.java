package com.khata.accountType.repositories;

import com.khata.accountType.entity.AccountCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AccountCategoryRepository extends JpaRepository<AccountCategory, Integer> {
    Optional<AccountCategory> findByName(String name);
}
