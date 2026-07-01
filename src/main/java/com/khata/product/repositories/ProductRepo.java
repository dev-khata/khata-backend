package com.khata.product.repositories;

import com.khata.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepo extends JpaRepository<Product, Integer> {

    @EntityGraph(attributePaths = {"departmentRates", "departmentRates.department"})
    Optional<Product> findByIdAndCreatedUserId(Integer id, Integer createdUserId);

    Optional<Product> findByProductCodeAndCreatedUserId(String productCode, Integer createdUserId);

    Page<Product> findByCreatedUserId(Integer createdUserId, Pageable pageable);

    Page<Product> findByProductNameContainingIgnoreCaseAndCreatedUserId(String productName, Integer createdUserId, Pageable pageable);
}
