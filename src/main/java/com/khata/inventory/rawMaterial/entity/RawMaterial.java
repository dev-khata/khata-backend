package com.khata.inventory.rawMaterial.entity;

import com.khata.inventory.rawMaterial.entity.enums.RawMaterialUnit;
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
        name = "raw_material",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"created_user_id", "material_code"})
        }
)
public class RawMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, name = "material_code", length = 50)
    private String materialCode;

    @Column(nullable = false, length = 100)
    private String materialName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RawMaterialUnit unit;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false, name = "created_user_id")
    private Integer createdUserId;

    @OneToMany(mappedBy = "rawMaterial", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RawMaterialStockBatch> stockBatches = new ArrayList<>();
}
