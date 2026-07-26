package com.khata.inventory.productionLots.service.impl;

import com.khata.auth.service.UserService;
import com.khata.exceptions.BadRequestException;
import com.khata.exceptions.ResourceAlreadyExistsException;
import com.khata.exceptions.ResourceNotFoundException;
import com.khata.inventory.productionLots.dto.ProductionLotAllocationDTO;
import com.khata.inventory.productionLots.dto.ProductionLotDTO;
import com.khata.inventory.productionLots.entity.ProductionLot;
import com.khata.inventory.productionLots.entity.ProductionLotAllocation;
import com.khata.inventory.productionLots.entity.ProductionLotStockIssue;
import com.khata.inventory.productionLots.entity.ProductionStage;
import com.khata.inventory.productionLots.repositories.ProductionLotRepo;
import com.khata.inventory.productionLots.service.ProductionLotService;
import com.khata.inventory.rawMaterial.entity.RawMaterial;
import com.khata.inventory.rawMaterial.entity.RawMaterialStockBatch;
import com.khata.inventory.rawMaterial.repositories.RawMaterialRepo;
import com.khata.inventory.rawMaterial.repositories.RawMaterialStockBatchRepo;
import com.khata.product.entity.Product;
import com.khata.product.repositories.ProductRepo;
import com.khata.settings.basicSettings.fiscalYear.entity.FiscalYear;
import com.khata.settings.basicSettings.fiscalYear.services.FiscalYearService;
import com.khata.utils.date.NepaliDateConversionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductionLotServiceImpl implements ProductionLotService {

    private final ProductionLotRepo productionLotRepo;
    private final RawMaterialRepo rawMaterialRepo;
    private final RawMaterialStockBatchRepo rawMaterialStockBatchRepo;
    private final ProductRepo productRepo;
    private final ProductionStageFlowResolver stageFlowResolver;
    private final FiscalYearService fiscalYearService;
    private final NepaliDateConversionService nepaliDateConversionService;
    private final UserService userService;

    @Override
    @Transactional
    public ProductionLotDTO createProductionLot(ProductionLotDTO productionLotDTO) {
        Integer currentUserId = userService.getCurrentUserId();
        RawMaterial rawMaterial = getRawMaterialEntityById(productionLotDTO.getMaterialId(), currentUserId);
        validateStockUnit(rawMaterial, productionLotDTO);
        validateDatePair(productionLotDTO);
        validateDuplicateProducts(productionLotDTO.getAllocations());

        FiscalYear fiscalYear = fiscalYearService.getOrCreateByDate(productionLotDTO.getStartDateInEnglish());
        ProductionLot productionLot = new ProductionLot();
        productionLot.setProductionLotNumber(generateProductionLotNumber());
        productionLot.setRawMaterial(rawMaterial);
        productionLot.setIssuedRolls(productionLotDTO.getIssuedRolls());
        productionLot.setIssuedStock(productionLotDTO.getIssuedStock());
        productionLot.setStockUnit(rawMaterial.getUnit());
        productionLot.setStartDateInEnglish(productionLotDTO.getStartDateInEnglish());
        productionLot.setStartDateInNepali(productionLotDTO.getStartDateInNepali());
        productionLot.setFiscalYear(fiscalYear);
        productionLot.setCreatedUserId(currentUserId);

        productionLotDTO.getAllocations().forEach(allocationDTO ->
                productionLot.getAllocations().add(buildAllocation(productionLot, allocationDTO, currentUserId)));
        initializeProductionLotCurrentStage(productionLot);
        issueRawMaterialStock(productionLot, currentUserId);

        ProductionLot savedProductionLot = productionLotRepo.save(productionLot);
        log.info("Production lot created | id={} | lotNumber={}",
                savedProductionLot.getId(), savedProductionLot.getProductionLotNumber());
        return toDTO(savedProductionLot);
    }

    @Override
    @Transactional
    public ProductionLotDTO updateProductionLot(Integer productionLotId, ProductionLotDTO productionLotDTO) {
        Integer currentUserId = userService.getCurrentUserId();
        ProductionLot productionLot = productionLotRepo.findByIdAndCreatedUserId(productionLotId, currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Production lot", "id", productionLotId));
        RawMaterial rawMaterial = getRawMaterialEntityById(productionLotDTO.getMaterialId(), currentUserId);
        validateStockUnit(rawMaterial, productionLotDTO);
        validateDatePair(productionLotDTO);
        validateDuplicateProducts(productionLotDTO.getAllocations());

        restoreIssuedStock(productionLot);
        productionLot.getStockIssues().clear();
        productionLot.getAllocations().clear();
        productionLotRepo.flush();

        FiscalYear fiscalYear = fiscalYearService.getOrCreateByDate(productionLotDTO.getStartDateInEnglish());
        productionLot.setRawMaterial(rawMaterial);
        productionLot.setIssuedRolls(productionLotDTO.getIssuedRolls());
        productionLot.setIssuedStock(productionLotDTO.getIssuedStock());
        productionLot.setStockUnit(rawMaterial.getUnit());
        productionLot.setStartDateInEnglish(productionLotDTO.getStartDateInEnglish());
        productionLot.setStartDateInNepali(productionLotDTO.getStartDateInNepali());
        productionLot.setFiscalYear(fiscalYear);

        productionLotDTO.getAllocations().forEach(allocationDTO ->
                productionLot.getAllocations().add(buildAllocation(productionLot, allocationDTO, currentUserId)));
        initializeProductionLotCurrentStage(productionLot);
        issueRawMaterialStock(productionLot, currentUserId);

        ProductionLot updatedProductionLot = productionLotRepo.save(productionLot);
        log.info("Production lot updated | id={} | lotNumber={}",
                updatedProductionLot.getId(), updatedProductionLot.getProductionLotNumber());
        return toDTO(updatedProductionLot);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductionLotDTO> getProductionLots(Integer fiscalYearId, Pageable pageable) {
        Integer currentUserId = userService.getCurrentUserId();
        Integer selectedFiscalYearId = fiscalYearId == null
                ? fiscalYearService.getOrCreateByDate(LocalDate.now()).getId()
                : fiscalYearId;
        Page<ProductionLot> productionLots =
                productionLotRepo.findByCreatedUserIdAndFiscalYearId(currentUserId, selectedFiscalYearId, pageable);
        return productionLots.map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductionLotDTO getProductionLotById(Integer productionLotId) {
        Integer currentUserId = userService.getCurrentUserId();
        ProductionLot productionLot = productionLotRepo.findByIdAndCreatedUserId(productionLotId, currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Production lot", "id", productionLotId));
        return toDTO(productionLot);
    }

    @Override
    @Transactional
    public void deleteProductionLot(Integer productionLotId) {
        Integer currentUserId = userService.getCurrentUserId();
        ProductionLot productionLot = productionLotRepo.findByIdAndCreatedUserId(productionLotId, currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Production lot", "id", productionLotId));

        restoreIssuedStock(productionLot);
        productionLotRepo.delete(productionLot);
        log.info("Production lot deleted | id={} | lotNumber={}",
                productionLot.getId(), productionLot.getProductionLotNumber());
    }

    private void issueRawMaterialStock(ProductionLot productionLot, Integer currentUserId) {
        List<RawMaterialStockBatch> availableBatches = rawMaterialStockBatchRepo.findAvailableBatchesForIssue(
                productionLot.getRawMaterial().getId(), currentUserId);

        Integer remainingRollsToIssue = productionLot.getIssuedRolls();
        BigDecimal remainingStockToIssue = productionLot.getIssuedStock();

        validateAvailableStock(availableBatches, remainingRollsToIssue, remainingStockToIssue);

        for (RawMaterialStockBatch stockBatch : availableBatches) {
            if (remainingRollsToIssue == 0 && remainingStockToIssue.compareTo(BigDecimal.ZERO) == 0) {
                break;
            }

            Integer batchRollsToIssue = Math.min(remainingRollsToIssue, stockBatch.getAvailableRolls());
            BigDecimal batchStockToIssue = remainingStockToIssue.min(stockBatch.getAvailableQuantity());

            if (batchRollsToIssue == 0 && batchStockToIssue.compareTo(BigDecimal.ZERO) == 0) {
                continue;
            }

            stockBatch.setAvailableRolls(stockBatch.getAvailableRolls() - batchRollsToIssue);
            stockBatch.setAvailableQuantity(stockBatch.getAvailableQuantity().subtract(batchStockToIssue));

            ProductionLotStockIssue stockIssue = new ProductionLotStockIssue();
            stockIssue.setProductionLot(productionLot);
            stockIssue.setStockBatch(stockBatch);
            stockIssue.setIssuedRolls(batchRollsToIssue);
            stockIssue.setIssuedStock(batchStockToIssue);
            productionLot.getStockIssues().add(stockIssue);

            remainingRollsToIssue -= batchRollsToIssue;
            remainingStockToIssue = remainingStockToIssue.subtract(batchStockToIssue);
        }
    }

    private void restoreIssuedStock(ProductionLot productionLot) {
        for (ProductionLotStockIssue stockIssue : productionLot.getStockIssues()) {
            RawMaterialStockBatch stockBatch = stockIssue.getStockBatch();
            stockBatch.setAvailableRolls(stockBatch.getAvailableRolls() + stockIssue.getIssuedRolls());
            stockBatch.setAvailableQuantity(stockBatch.getAvailableQuantity().add(stockIssue.getIssuedStock()));
        }
    }

    private void validateAvailableStock(
            List<RawMaterialStockBatch> availableBatches,
            Integer issuedRolls,
            BigDecimal issuedStock) {
        int availableRolls = availableBatches.stream()
                .mapToInt(RawMaterialStockBatch::getAvailableRolls)
                .sum();
        BigDecimal availableStock = availableBatches.stream()
                .map(RawMaterialStockBatch::getAvailableQuantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (availableRolls < issuedRolls) {
            throw new BadRequestException("Insufficient raw material rolls for production lot.");
        }

        if (availableStock.compareTo(issuedStock) < 0) {
            throw new BadRequestException("Insufficient raw material stock for production lot.");
        }
    }

    private ProductionLotAllocation buildAllocation(
            ProductionLot productionLot,
            ProductionLotAllocationDTO allocationDTO,
            Integer currentUserId) {
        Product product = productRepo.findByIdAndCreatedUserId(allocationDTO.getProductId(), currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", allocationDTO.getProductId()));

        ProductionLotAllocation allocation = new ProductionLotAllocation();
        allocation.setProductionLot(productionLot);
        allocation.setProduct(product);
        stageFlowResolver.getConfiguredProductFlow(product, currentUserId).stream()
                .findFirst()
                .ifPresent(allocation::setCurrentStage);
        return allocation;
    }

    private void initializeProductionLotCurrentStage(ProductionLot productionLot) {
        productionLot.getAllocations().stream()
                .map(ProductionLotAllocation::getCurrentStage)
                .filter(stage -> stage != null)
                .min(Comparator.comparing(ProductionStage::getDisplayOrder).thenComparing(ProductionStage::getId))
                .map(ProductionStage::getStageName)
                .ifPresentOrElse(productionLot::setCurrentStage, () -> productionLot.setCurrentStage("Pending"));
    }

    private RawMaterial getRawMaterialEntityById(Integer materialId, Integer currentUserId) {
        return rawMaterialRepo.findByIdAndCreatedUserId(materialId, currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Raw material", "id", materialId));
    }

    private void validateStockUnit(RawMaterial rawMaterial, ProductionLotDTO productionLotDTO) {
        if (!rawMaterial.getUnit().equals(productionLotDTO.getStockUnit())) {
            throw new BadRequestException("Stock unit must match raw material unit.");
        }
    }

    private void validateDatePair(ProductionLotDTO productionLotDTO) {
        LocalDate convertedEnglishDate;
        try {
            convertedEnglishDate = nepaliDateConversionService.toEnglishDate(productionLotDTO.getStartDateInNepali());
        } catch (RuntimeException ex) {
            throw new BadRequestException("Nepali date is invalid.");
        }

        if (!convertedEnglishDate.equals(productionLotDTO.getStartDateInEnglish())) {
            throw new BadRequestException("Nepali date does not match English date.");
        }
    }

    private void validateDuplicateProducts(List<ProductionLotAllocationDTO> allocations) {
        Set<Integer> productIds = new HashSet<>();
        for (ProductionLotAllocationDTO allocation : allocations) {
            if (!productIds.add(allocation.getProductId())) {
                throw new ResourceAlreadyExistsException("Production lot product allocation", allocation.getProductId());
            }
        }
    }

    private String generateProductionLotNumber() {
        return String.format("Lot-%04d", productionLotRepo.getNextProductionLotSequence());
    }

    private ProductionLotDTO toDTO(ProductionLot productionLot) {
        ProductionLotDTO productionLotDTO = new ProductionLotDTO();
        productionLotDTO.setId(productionLot.getId());
        productionLotDTO.setProductionLotNumber(productionLot.getProductionLotNumber());
        productionLotDTO.setMaterialId(productionLot.getRawMaterial().getId());
        productionLotDTO.setMaterialName(productionLot.getRawMaterial().getMaterialName());
        productionLotDTO.setIssuedRolls(productionLot.getIssuedRolls());
        productionLotDTO.setIssuedStock(productionLot.getIssuedStock());
        productionLotDTO.setStockUnit(productionLot.getStockUnit());
        productionLotDTO.setStartDateInEnglish(productionLot.getStartDateInEnglish());
        productionLotDTO.setStartDateInNepali(productionLot.getStartDateInNepali());
        productionLotDTO.setCurrentStage(productionLot.getCurrentStage());
        productionLotDTO.setStatus(productionLot.getStatus().getLabel());
        productionLotDTO.setCompleteDateInEnglish(productionLot.getCompleteDateInEnglish());
        productionLotDTO.setCompleteDateInNepali(productionLot.getCompleteDateInNepali());
        productionLotDTO.setFiscalYearId(productionLot.getFiscalYear().getId());
        productionLotDTO.setAllocations(productionLot.getAllocations().stream()
                .map(this::toAllocationDTO)
                .toList());
        return productionLotDTO;
    }

    private ProductionLotAllocationDTO toAllocationDTO(ProductionLotAllocation allocation) {
        Product product = allocation.getProduct();
        ProductionLotAllocationDTO allocationDTO = new ProductionLotAllocationDTO();
        allocationDTO.setProductId(product.getId());
        allocationDTO.setProductCode(product.getProductCode());
        allocationDTO.setProductName(product.getProductName());
        allocationDTO.setStatus(allocation.getStatus().getLabel());
        if (allocation.getCurrentStage() != null) {
            allocationDTO.setCurrentStageId(allocation.getCurrentStage().getId());
            allocationDTO.setCurrentStageName(allocation.getCurrentStage().getStageName());
        }
        allocationDTO.setCompleteDateInEnglish(allocation.getCompleteDateInEnglish());
        allocationDTO.setCompleteDateInNepali(allocation.getCompleteDateInNepali());
        return allocationDTO;
    }
}
