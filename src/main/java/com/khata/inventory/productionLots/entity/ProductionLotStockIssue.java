package com.khata.inventory.productionLots.entity;

import com.khata.inventory.rawMaterial.entity.RawMaterialStockBatch;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "production_lot_stock_issue")
public class ProductionLotStockIssue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "production_lot_id", nullable = false)
    private ProductionLot productionLot;

    @ManyToOne
    @JoinColumn(name = "stock_batch_id", nullable = false)
    private RawMaterialStockBatch stockBatch;

    @Column(nullable = false)
    private Integer issuedRolls;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal issuedStock;
}
