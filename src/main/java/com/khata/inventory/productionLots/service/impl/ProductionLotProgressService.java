package com.khata.inventory.productionLots.service.impl;

import com.khata.inventory.productionLots.entity.ProductionLot;
import com.khata.inventory.productionLots.entity.ProductionLotAllocation;
import com.khata.inventory.productionLots.entity.ProductionLotStageEntry;
import com.khata.inventory.productionLots.entity.ProductionStage;
import com.khata.inventory.productionLots.entity.enums.ProductionLotStatus;
import com.khata.inventory.productionLots.repositories.ProductionLotRepo;
import com.khata.inventory.productionLots.repositories.ProductionLotStageEntryRepo;
import com.khata.product.entity.Product;
import com.khata.utils.date.NepaliDateConversionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductionLotProgressService {

    private static final String PENDING_STAGE = "Pending";
    private static final String COMPLETED_STAGE = "Completed";

    private final ProductionLotRepo productionLotRepo;
    private final ProductionLotStageEntryRepo stageEntryRepo;
    private final ProductionStageFlowResolver stageFlowResolver;
    private final NepaliDateConversionService nepaliDateConversionService;

    @Transactional
    public void updateProgress(ProductionLot productionLot, Integer currentUserId) {
        boolean allProductsCompleted = true;
        ProductionStage lotCurrentStage = null;
        LocalDate today = LocalDate.now();
        String nepaliToday = nepaliDateConversionService.toNepaliDate(today);

        for (ProductionLotAllocation allocation : productionLot.getAllocations()) {
            Product allocatedProduct = allocation.getProduct();
            List<ProductionStage> productFlow = stageFlowResolver.getConfiguredProductFlow(
                    allocatedProduct,
                    currentUserId);
            ProductProgress progress = getProductProgress(
                    productionLot.getId(),
                    allocatedProduct,
                    productFlow,
                    currentUserId);

            if (progress.completed()) {
                markAllocationCompleted(allocation, productFlow, today, nepaliToday);
                continue;
            }

            allProductsCompleted = false;
            markAllocationInProgress(allocation, progress.currentStage());
            if (lotCurrentStage == null) {
                lotCurrentStage = progress.currentStage();
            }
        }

        if (allProductsCompleted && !productionLot.getAllocations().isEmpty()) {
            markLotCompleted(productionLot, today, nepaliToday);
        } else {
            markLotInProgress(productionLot, lotCurrentStage);
        }
        productionLotRepo.save(productionLot);
    }

    private ProductProgress getProductProgress(
            Integer productionLotId,
            Product product,
            List<ProductionStage> productFlow,
            Integer currentUserId) {
        if (productFlow.isEmpty()) {
            return new ProductProgress(false, null);
        }

        Set<Integer> productDepartmentIds = stageFlowResolver.getProductDepartmentIds(product);
        for (ProductionStage productionStage : productFlow) {
            StageCompletion completion = getStageCompletion(
                    productionLotId,
                    product.getId(),
                    productionStage,
                    productDepartmentIds,
                    currentUserId);
            if (!completion.completed()) {
                return new ProductProgress(false, productionStage);
            }
        }
        return new ProductProgress(true, productFlow.get(productFlow.size() - 1));
    }

    public StageCompletion getStageCompletion(
            Integer productionLotId,
            Integer productId,
            ProductionStage productionStage,
            Set<Integer> productDepartmentIds,
            Integer currentUserId) {
        List<ProductionLotStageEntry> entries = stageEntryRepo
                .findByProductionLotIdAndProductIdAndProductionStageIdAndCreatedUserIdOrderByIdAsc(
                        productionLotId,
                        productId,
                        productionStage.getId(),
                        currentUserId);
        if (entries.isEmpty()) {
            return new StageCompletion(null, false);
        }

        Optional<ProductionLotStageEntry> stageLevelEntry = entries.stream()
                .filter(entry -> entry.getDepartment() == null)
                .findFirst();
        if (stageLevelEntry.isPresent()) {
            ProductionLotStageEntry entry = stageLevelEntry.get();
            return new StageCompletion(entry.getCompletedPieces(), Boolean.TRUE.equals(entry.getCompleted()));
        }

        Set<Integer> requiredDepartmentIds = productionStage.getDepartmentMappings().stream()
                .map(mapping -> mapping.getDepartment().getId())
                .filter(productDepartmentIds::contains)
                .collect(Collectors.toSet());
        Set<Integer> completedDepartmentIds = entries.stream()
                .filter(entry -> entry.getDepartment() != null)
                .filter(entry -> Boolean.TRUE.equals(entry.getCompleted()))
                .map(entry -> entry.getDepartment().getId())
                .collect(Collectors.toSet());
        boolean completed = !requiredDepartmentIds.isEmpty() && completedDepartmentIds.containsAll(requiredDepartmentIds);
        Integer completedPieces = entries.stream()
                .map(ProductionLotStageEntry::getCompletedPieces)
                .filter(completedPiece -> completedPiece != null)
                .min(Integer::compareTo)
                .orElse(null);
        return new StageCompletion(completedPieces, completed);
    }

    private void markAllocationCompleted(
            ProductionLotAllocation allocation,
            List<ProductionStage> productFlow,
            LocalDate today,
            String nepaliToday) {
        allocation.setStatus(ProductionLotStatus.COMPLETED);
        allocation.setCurrentStage(productFlow.isEmpty() ? null : productFlow.get(productFlow.size() - 1));
        if (allocation.getCompleteDateInEnglish() == null) {
            allocation.setCompleteDateInEnglish(today);
            allocation.setCompleteDateInNepali(nepaliToday);
        }
    }

    private void markAllocationInProgress(
            ProductionLotAllocation allocation,
            ProductionStage currentStage) {
        allocation.setStatus(ProductionLotStatus.IN_PROGRESS);
        allocation.setCurrentStage(currentStage);
        allocation.setCompleteDateInEnglish(null);
        allocation.setCompleteDateInNepali(null);
    }

    private void markLotCompleted(
            ProductionLot productionLot,
            LocalDate today,
            String nepaliToday) {
        productionLot.setStatus(ProductionLotStatus.COMPLETED);
        productionLot.setCurrentStage(COMPLETED_STAGE);
        if (productionLot.getCompleteDateInEnglish() == null) {
            productionLot.setCompleteDateInEnglish(today);
            productionLot.setCompleteDateInNepali(nepaliToday);
        }
    }

    private void markLotInProgress(
            ProductionLot productionLot,
            ProductionStage currentStage) {
        productionLot.setStatus(ProductionLotStatus.IN_PROGRESS);
        productionLot.setCompleteDateInEnglish(null);
        productionLot.setCompleteDateInNepali(null);
        productionLot.setCurrentStage(currentStage == null ? PENDING_STAGE : currentStage.getStageName());
    }

    public record StageCompletion(Integer completedPieces, boolean completed) {
    }

    private record ProductProgress(boolean completed, ProductionStage currentStage) {
    }
}
