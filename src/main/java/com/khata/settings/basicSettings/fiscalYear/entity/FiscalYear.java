package com.khata.settings.basicSettings.fiscalYear.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(
        name = "fiscal_year",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_fiscal_year_name", columnNames = {"fiscal_year_name"})
        }
)
public class FiscalYear {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, name = "fiscal_year_name", length = 20)
    private String fiscalYearName;

    @Column(nullable = false, name = "start_date_nepali", length = 10)
    private String startDateNepali;

    @Column(nullable = false, name = "end_date_nepali", length = 10)
    private String endDateNepali;

    @Column(nullable = false, name = "start_date_english")
    private LocalDate startDateEnglish;

    @Column(nullable = false, name = "end_date_english")
    private LocalDate endDateEnglish;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private boolean closed = false;

    @Column(nullable = false, name = "created_user_id")
    private Integer createdUserId;
}
