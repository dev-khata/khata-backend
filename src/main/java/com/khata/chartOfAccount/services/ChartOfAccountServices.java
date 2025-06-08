package com.khata.chartOfAccount.services;

import com.khata.chartOfAccount.dto.ChartOfAccountDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChartOfAccountServices {
    ChartOfAccountDTO createChartOfAccount(ChartOfAccountDTO chartOfAccountDTO);
    ChartOfAccountDTO updateChartOfAccount(ChartOfAccountDTO chartOfAccountDTO, Integer chartOfAccountId);
    ChartOfAccountDTO getChartOfAccountById(Integer chartOfAccountId);
    Page<ChartOfAccountDTO> getChartOfAccounts(Pageable pageable);
    void deleteChartOfAccount(Integer chartOfAccountId);
}
