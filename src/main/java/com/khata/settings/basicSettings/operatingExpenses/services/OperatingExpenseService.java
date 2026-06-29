package com.khata.settings.basicSettings.operatingExpenses.services;

import com.khata.settings.basicSettings.operatingExpenses.dto.OperatingExpenseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OperatingExpenseService {
    List<OperatingExpenseDTO> upsertOperatingExpenses(List<OperatingExpenseDTO> operatingExpenseDTOs);

    Page<OperatingExpenseDTO> getOperatingExpenses(Pageable pageable);

    void deleteOperatingExpense(Integer operatingExpenseId);
}
