package com.khata.settings.basicSettings.operatingExpenses.repositories;

import com.khata.settings.basicSettings.operatingExpenses.entity.OperatingExpense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OperatingExpenseRepo extends JpaRepository<OperatingExpense, Integer> {
    Optional<OperatingExpense> findByIdAndCreatedUserId(Integer id, Integer createdUserId);

    Optional<OperatingExpense> findByExpenseNameAndCreatedUserId(String expenseName, Integer createdUserId);

    Page<OperatingExpense> findByCreatedUserId(Integer createdUserId, Pageable pageable);
}
