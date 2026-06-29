package com.khata.settings.basicSettings.operatingExpenses.entity;

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

import java.math.BigDecimal;

@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(
        name = "operating_expense",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"created_user_id", "expense_name"})
        }
)
public class OperatingExpense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, name = "expense_name", length = 100)
    private String expenseName;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amountPerPiece;

    @Column(nullable = false, name = "created_user_id")
    private Integer createdUserId;
}
