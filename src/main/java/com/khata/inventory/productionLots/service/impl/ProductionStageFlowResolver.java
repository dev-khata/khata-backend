package com.khata.inventory.productionLots.service.impl;

import com.khata.inventory.productionLots.entity.ProductionStage;
import com.khata.inventory.productionLots.repositories.ProductionStageRepo;
import com.khata.product.entity.Product;
import com.khata.product.entity.ProductDepartmentRate;
import com.khata.settings.department.entity.Department;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductionStageFlowResolver {

    private final ProductionStageRepo productionStageRepo;

    public List<ProductionStage> getConfiguredProductFlow(Product product, Integer currentUserId) {
        Set<Integer> productDepartmentIds = getProductDepartmentIds(product);
        if (productDepartmentIds.isEmpty()) {
            return List.of();
        }

        return productionStageRepo.findByCreatedUserIdOrderByDisplayOrderAscIdAsc(currentUserId).stream()
                .filter(stage -> stage.getDepartmentMappings().stream()
                        .anyMatch(mapping -> productDepartmentIds.contains(mapping.getDepartment().getId())))
                .sorted(Comparator.comparing(ProductionStage::getDisplayOrder).thenComparing(ProductionStage::getId))
                .toList();
    }

    public Set<Integer> getProductDepartmentIds(Product product) {
        return product.getDepartmentRates().stream()
                .map(ProductDepartmentRate::getDepartment)
                .map(Department::getId)
                .collect(Collectors.toSet());
    }
}
