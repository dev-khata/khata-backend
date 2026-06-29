package com.khata.settings.basicSettings.fiscalYear.repositories;

import com.khata.settings.basicSettings.fiscalYear.entity.FiscalYear;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface FiscalYearRepo extends JpaRepository<FiscalYear, Integer> {

    Optional<FiscalYear> findByFiscalYearName(String fiscalYearName);

    @Query("""
            SELECT fiscalYear
            FROM FiscalYear fiscalYear
            WHERE :englishDate BETWEEN fiscalYear.startDateEnglish AND fiscalYear.endDateEnglish
            """)
    Optional<FiscalYear> findByEnglishDate(@Param("englishDate") LocalDate englishDate);
}
