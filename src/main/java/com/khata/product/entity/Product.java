package com.khata.product.entity;

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
        name = "product",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"created_user_id", "product_code"})
        }
)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, name = "product_code", length = 50)
    private String productCode;

    @Column(nullable = false, length = 100)
    private String productName;

    @Column(nullable = false, name = "created_user_id")
    private Integer createdUserId;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductDepartmentRate> departmentRates = new ArrayList<>();
}
