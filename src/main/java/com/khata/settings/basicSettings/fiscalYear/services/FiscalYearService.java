package com.khata.settings.basicSettings.fiscalYear.services;

import com.khata.settings.basicSettings.fiscalYear.dto.FiscalYearDTO;
import com.khata.settings.basicSettings.fiscalYear.entity.FiscalYear;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface FiscalYearService {

    FiscalYearDTO getCurrentFiscalYear();

    Page<FiscalYearDTO> getFiscalYears(Pageable pageable);

    FiscalYear getOrCreateByDate(LocalDate englishDate);

    FiscalYear findByDate(LocalDate englishDate);
}
