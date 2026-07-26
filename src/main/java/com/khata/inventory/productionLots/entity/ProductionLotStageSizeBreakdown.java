package com.khata.inventory.productionLots.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "production_lot_stage_size_breakdown")
public class ProductionLotStageSizeBreakdown {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stage_entry_id", nullable = false)
    private ProductionLotStageEntry stageEntry;

    @Column(nullable = false, length = 30)
    private String size;

    @Column(nullable = false)
    private Integer pieces;
}
