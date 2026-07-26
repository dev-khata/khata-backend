package com.khata.inventory.productionLots.entity;

import com.khata.inventory.productionLots.entity.enums.ProductionStageWorkType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(
        name = "production_stage",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_production_stage_user_code",
                        columnNames = {"created_user_id", "stage_code"}),
                @UniqueConstraint(
                        name = "uk_production_stage_user_display_order",
                        columnNames = {"created_user_id", "display_order"})
        }
)
public class ProductionStage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, name = "stage_code", length = 50)
    private String stageCode;

    @Column(nullable = false, name = "stage_name", length = 100)
    private String stageName;

    @Column(nullable = false, name = "display_order")
    private Integer displayOrder;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "work_type", length = 50)
    private ProductionStageWorkType workType = ProductionStageWorkType.GENERIC;

    @Column(nullable = false, name = "created_user_id")
    private Integer createdUserId;

    @OrderBy("id ASC")
    @OneToMany(mappedBy = "productionStage", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductionStageDepartmentMapping> departmentMappings = new ArrayList<>();
}
