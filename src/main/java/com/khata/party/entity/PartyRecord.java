package com.khata.party.entity;

import com.khata.party.entity.enums.TransactionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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

    @Column(nullable = false)
    private LocalDate nepaliDate;

    @Column(nullable = false)
    private LocalDate englishDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 100)
    private TransactionType transactionType;

    @ManyToOne
    @JoinColumn(name = "party_id")
    private Party party;
}
