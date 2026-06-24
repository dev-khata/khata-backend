package com.khata.party.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PartyRecordSummaryDTO {
    private BigDecimal totalDebit;
    private BigDecimal totalCredit;
    private BigDecimal netBalance;
}
