package com.khata.settings.basicSettings.fiscalYear.services.impl;

import com.khata.auth.service.UserService;
import com.khata.exceptions.ResourceNotFoundException;
import com.khata.settings.basicSettings.fiscalYear.dto.FiscalYearDTO;
import com.khata.settings.basicSettings.fiscalYear.entity.FiscalYear;
import com.khata.settings.basicSettings.fiscalYear.repositories.FiscalYearRepo;
import com.khata.settings.basicSettings.fiscalYear.services.FiscalYearService;
import com.khata.utils.date.NepaliDateConversionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Slf4j
@RequiredArgsConstructor
public class FiscalYearServiceImpl implements FiscalYearService {

    private static final int FISCAL_YEAR_START_MONTH = 4;
    private static final int FISCAL_YEAR_START_DAY = 1;
    private static final int FISCAL_YEAR_END_MONTH = 3;
    private static final int FISCAL_YEAR_END_DAY = 31;

    private final FiscalYearRepo fiscalYearRepo;
    private final UserService userService;
    private final NepaliDateConversionService nepaliDateConversionService;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public FiscalYearDTO getCurrentFiscalYear() {
        FiscalYear fiscalYear = getOrCreateByDate(LocalDate.now());
        return toDTO(fiscalYear);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FiscalYearDTO> getFiscalYears(Pageable pageable) {
        Page<FiscalYear> fiscalYears = fiscalYearRepo.findAll(pageable);
        return fiscalYears.map(this::toDTO);
    }

    @Override
    @Transactional
    public FiscalYear getOrCreateByDate(LocalDate englishDate) {
        Integer currentUserId = userService.getCurrentUserId();
        FiscalYearCalculation calculation = calculateFiscalYear(englishDate);

        return fiscalYearRepo
                .findByFiscalYearName(calculation.fiscalYearName())
                .orElseGet(() -> createFiscalYear(calculation, currentUserId));
    }

    @Override
    @Transactional(readOnly = true)
    public FiscalYear findByDate(LocalDate englishDate) {
        return fiscalYearRepo.findByEnglishDate(englishDate).orElseThrow(
                () -> new ResourceNotFoundException("Fiscal year", "date", englishDate.toString()));
    }

    private FiscalYear createFiscalYear(FiscalYearCalculation calculation, Integer currentUserId) {
        FiscalYear fiscalYear = new FiscalYear();
        fiscalYear.setFiscalYearName(calculation.fiscalYearName());
        fiscalYear.setStartDateNepali(calculation.startDateNepali());
        fiscalYear.setEndDateNepali(calculation.endDateNepali());
        fiscalYear.setStartDateEnglish(calculation.startDateEnglish());
        fiscalYear.setEndDateEnglish(calculation.endDateEnglish());
        fiscalYear.setActive(true);
        fiscalYear.setClosed(false);
        fiscalYear.setCreatedUserId(currentUserId);

        FiscalYear savedFiscalYear = fiscalYearRepo.save(fiscalYear);
        log.info("Fiscal year auto-created | name={} | userId={}", savedFiscalYear.getFiscalYearName(), currentUserId);
        return savedFiscalYear;
    }

    private FiscalYearCalculation calculateFiscalYear(LocalDate englishDate) {
        String nepaliDate = nepaliDateConversionService.toNepaliDate(englishDate);
        NepaliDateParts nepaliDateParts = parseNepaliDate(nepaliDate);

        int fiscalStartYear = nepaliDateParts.month() >= FISCAL_YEAR_START_MONTH
                ? nepaliDateParts.year()
                : nepaliDateParts.year() - 1;
        int fiscalEndYear = fiscalStartYear + 1;

        String fiscalYearName = fiscalStartYear + "/" + String.format("%02d", fiscalEndYear % 100);
        String startDateNepali = formatNepaliDate(fiscalStartYear, FISCAL_YEAR_START_MONTH, FISCAL_YEAR_START_DAY);
        String endDateNepali = formatNepaliDate(fiscalEndYear, FISCAL_YEAR_END_MONTH, FISCAL_YEAR_END_DAY);

        return new FiscalYearCalculation(
                fiscalYearName,
                startDateNepali,
                endDateNepali,
                nepaliDateConversionService.toEnglishDate(startDateNepali),
                nepaliDateConversionService.toEnglishDate(endDateNepali));
    }

    private NepaliDateParts parseNepaliDate(String nepaliDate) {
        String[] dateParts = nepaliDate.split("-");
        if (dateParts.length != 3) {
            throw new IllegalStateException("Invalid Nepali date returned by converter: " + nepaliDate);
        }

        return new NepaliDateParts(Integer.parseInt(dateParts[0]), Integer.parseInt(dateParts[1]));
    }

    private String formatNepaliDate(int year, int month, int day) {
        return String.format("%d-%02d-%02d", year, month, day);
    }

    private FiscalYearDTO toDTO(FiscalYear fiscalYear) {
        return modelMapper.map(fiscalYear, FiscalYearDTO.class);
    }

    private record NepaliDateParts(int year, int month) {
    }

    private record FiscalYearCalculation(
            String fiscalYearName,
            String startDateNepali,
            String endDateNepali,
            LocalDate startDateEnglish,
            LocalDate endDateEnglish) {
    }
}
