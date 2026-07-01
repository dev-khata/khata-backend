package com.khata.settings.department.entity;

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
        name = "department",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_department_user_code", columnNames = {"created_user_id", "department_code"})
        }
)
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, name = "department_code", length = 50)
    private String departmentCode;

    @Column(nullable = false, length = 100)
    private String departmentName;

    @Column(nullable = false, name = "piece_rate_enabled", columnDefinition = "boolean default false")
    private Boolean pieceRateEnabled = false;

    @Column(name = "default_piece_rate", precision = 19, scale = 2)
    private BigDecimal defaultPieceRate;

    @Column(nullable = false, name = "created_user_id")
    private Integer createdUserId;
}
