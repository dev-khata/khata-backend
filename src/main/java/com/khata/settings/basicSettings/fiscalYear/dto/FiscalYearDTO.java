package com.khata.settings.basicSettings.fiscalYear.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
public class FiscalYearDTO {

    private Integer id;

    private String fiscalYearName;

    private String startDateNepali;

    private String endDateNepali;

    private LocalDate startDateEnglish;

    private LocalDate endDateEnglish;

    private boolean active;

    private boolean closed;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer createdUserId;
}
