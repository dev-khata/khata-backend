package com.khata.inventory.productionLots.entity;

import com.khata.settings.department.entity.Department;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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

@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(
        name = "production_stage_department_mapping",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_production_stage_department",
                        columnNames = {"production_stage_id", "department_id"})
        }
)
public class ProductionStageDepartmentMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "production_stage_id", nullable = false)
    private ProductionStage productionStage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Column(nullable = false, name = "created_user_id")
    private Integer createdUserId;
}
