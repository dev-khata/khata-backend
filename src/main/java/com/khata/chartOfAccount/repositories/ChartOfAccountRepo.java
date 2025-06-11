package com.khata.chartOfAccount.repositories;

import com.khata.chartOfAccount.entity.ChartOfAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ChartOfAccountRepo extends JpaRepository<ChartOfAccount, Integer> {
    boolean existsByName(String name);
    Optional<ChartOfAccount> findByName(String name);
}
