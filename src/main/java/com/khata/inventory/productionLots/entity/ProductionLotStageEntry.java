package com.khata.inventory.productionLots.entity;

import com.khata.inventory.productionLots.entity.enums.ProductionStageWorkType;
import com.khata.product.entity.Product;
import com.khata.settings.department.entity.Department;
import com.khata.staff.employee.entity.Employee;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "production_lot_stage_entry")
public class ProductionLotStageEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "production_lot_id", nullable = false)
    private ProductionLot productionLot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "production_stage_id", nullable = false)
    private ProductionStage productionStage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "work_type", length = 50)
    private ProductionStageWorkType workType;

    @Column(name = "expected_pieces")
    private Integer expectedPieces;

    @Column(nullable = false, name = "completed_pieces")
    private Integer completedPieces;

    @Column(nullable = false, name = "extra_pieces")
    private Integer extraPieces = 0;

    @Column(nullable = false, name = "damage_pieces")
    private Integer damagePieces = 0;

    @Column(nullable = false, name = "add_to_master")
    private Boolean addToMaster = false;

    @Column(nullable = false, name = "deduct_from_master")
    private Boolean deductFromMaster = false;

    @Column(length = 500)
    private String remarks;

    @Column(nullable = false)
    private Boolean completed = false;

    @Column(nullable = false, name = "created_user_id")
    private Integer createdUserId;

    @Column(nullable = false, name = "created_at")
    private LocalDateTime createdAt;

    @Column(nullable = false, name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "stageEntry", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductionLotStageEntryEmployee> employees = new ArrayList<>();

    @OneToMany(mappedBy = "stageEntry", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductionLotStageSizeBreakdown> sizeBreakdowns = new ArrayList<>();

    @PrePersist
    void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
