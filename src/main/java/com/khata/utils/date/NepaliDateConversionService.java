package com.khata.utils.date;

import com.lishal.convert_to_nepali_date.NepaliDateConverter;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class NepaliDateConversionService {

    public String toNepaliDate(LocalDate englishDate) {
        return NepaliDateConverter.toNepaliDate(englishDate).format();
    }

    public LocalDate toEnglishDate(String nepaliDate) {
        return NepaliDateConverter.toEnglishDate(nepaliDate);
    }
}
