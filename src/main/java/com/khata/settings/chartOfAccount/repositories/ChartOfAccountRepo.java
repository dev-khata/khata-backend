package com.khata.settings.chartOfAccount.repositories;

import com.khata.settings.chartOfAccount.entity.ChartOfAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ChartOfAccountRepo extends JpaRepository<ChartOfAccount, Integer> {
    boolean existsByName(String name);
    Optional<ChartOfAccount> findByName(String name);
}
