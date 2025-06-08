package com.khata.accountType.repositories;

import com.khata.accountType.entity.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountTypeRepo extends JpaRepository<AccountType, Integer> {
    boolean existsByName(String name);
    Optional<AccountType> findByName(String name);
}
