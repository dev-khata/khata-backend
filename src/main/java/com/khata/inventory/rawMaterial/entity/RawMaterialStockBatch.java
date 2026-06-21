package com.khata.inventory.rawMaterial.entity;

import com.khata.party.entity.Party;
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
@Table(name = "raw_material_stock_batch")
public class RawMaterialStockBatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    private String batchNumber;

    @ManyToOne
    @JoinColumn(name = "raw_material_id", nullable = false)
    private RawMaterial rawMaterial;

    @ManyToOne
    @JoinColumn(name = "party_id", nullable = false)
    private Party party;

    @ManyToOne
    @JoinColumn(name = "fiscal_year_id", nullable = false)
    private FiscalYear fiscalYear;

    @Column(nullable = false, length = 10)
    private String purchaseDateNepali;

    @Column(nullable = false)
    private LocalDate purchaseDateEnglish;

    @Column(nullable = false)
    private Integer totalRollsPurchased;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal totalQuantityPurchased;

    @Column(nullable = false)
    private Integer availableRolls;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal availableQuantity;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal purchaseRate;

    @Column(nullable = false, name = "created_user_id")
    private Integer createdUserId;
}
