package com.khata.party.entity;

import com.khata.party.entity.enums.TransactionType;
import com.khata.settings.basicSettings.fiscalYear.entity.FiscalYear;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@NoArgsConstructor
@Entity
@Getter
@Setter
public class PartyRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String particular;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 10)
    private String nepaliDate;

    @Column(nullable = false)
    private LocalDate englishDate;

    @ManyToOne
    @JoinColumn(name = "fiscal_year_id", nullable = false)
    private FiscalYear fiscalYear;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 100)
    private TransactionType transactionType;

    @ManyToOne
    @JoinColumn(name = "party_id")
    private Party party;
}
