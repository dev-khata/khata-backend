package com.khata.inventory.productionLots.entity;

import com.khata.inventory.productionLots.entity.enums.ProductionLotStatus;
import com.khata.product.entity.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
        name = "production_lot_allocation",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"production_lot_id", "product_id"})
        }
)
public class ProductionLotAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "production_lot_id", nullable = false)
    private ProductionLot productionLot;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ProductionLotStatus status = ProductionLotStatus.IN_PROGRESS;

    @ManyToOne
    @JoinColumn(name = "current_stage_id")
    private ProductionStage currentStage;

    @Column(name = "complete_date_in_english")
    private LocalDate completeDateInEnglish;

    @Column(name = "complete_date_in_nepali", length = 10)
    private String completeDateInNepali;
}
