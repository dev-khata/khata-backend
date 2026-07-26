package com.khata.inventory.productionLots.entity;

import com.khata.inventory.productionLots.entity.enums.ProductionLotStatus;
import com.khata.inventory.rawMaterial.entity.RawMaterial;
import com.khata.inventory.rawMaterial.entity.enums.RawMaterialUnit;
import com.khata.settings.basicSettings.fiscalYear.entity.FiscalYear;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(
        name = "production_lot",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"production_lot_number"})
        }
)
public class ProductionLot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, name = "production_lot_number", length = 50)
    private String productionLotNumber;

    @ManyToOne
    @JoinColumn(name = "raw_material_id", nullable = false)
    private RawMaterial rawMaterial;

    @Column(nullable = false)
    private Integer issuedRolls;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal issuedStock;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RawMaterialUnit stockUnit;

    @Column(nullable = false, name = "start_date_in_english")
    private LocalDate startDateInEnglish;

    @Column(nullable = false, name = "start_date_in_nepali", length = 10)
    private String startDateInNepali;

    @Column(nullable = false, length = 100)
    private String currentStage = "Pending";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ProductionLotStatus status = ProductionLotStatus.IN_PROGRESS;

    @Column(name = "complete_date_in_english")
    private LocalDate completeDateInEnglish;

    @Column(name = "complete_date_in_nepali", length = 10)
    private String completeDateInNepali;

    @ManyToOne
    @JoinColumn(name = "fiscal_year_id", nullable = false)
    private FiscalYear fiscalYear;

    @Column(nullable = false, name = "created_user_id")
    private Integer createdUserId;

    @OneToMany(mappedBy = "productionLot", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductionLotAllocation> allocations = new ArrayList<>();

    @OneToMany(mappedBy = "productionLot", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductionLotStockIssue> stockIssues = new ArrayList<>();
}
