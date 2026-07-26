package com.khata.inventory.productionLots.service.impl;

import com.khata.auth.service.UserService;
import com.khata.exceptions.BadRequestException;
import com.khata.exceptions.ResourceNotFoundException;
import com.khata.inventory.productionLots.dto.ProductionLotStageEntryDTO;
import com.khata.inventory.productionLots.dto.ProductionLotStageEntryContextDTO;
import com.khata.inventory.productionLots.dto.ProductionLotStageEntryEmployeeDTO;
import com.khata.inventory.productionLots.dto.ProductionLotStageSizeBreakdownDTO;
import com.khata.inventory.productionLots.entity.ProductionLot;
import com.khata.inventory.productionLots.entity.ProductionLotStageEntry;
import com.khata.inventory.productionLots.entity.ProductionLotStageEntryEmployee;
import com.khata.inventory.productionLots.entity.ProductionLotStageSizeBreakdown;
import com.khata.inventory.productionLots.entity.ProductionStage;
import com.khata.inventory.productionLots.entity.enums.ProductionStageWorkType;
import com.khata.inventory.productionLots.mapper.ProductionLotStageEntryMapper;
import com.khata.inventory.productionLots.repositories.ProductionLotRepo;
import com.khata.inventory.productionLots.repositories.ProductionLotStageEntryRepo;
import com.khata.inventory.productionLots.repositories.ProductionStageRepo;
import com.khata.inventory.productionLots.service.ProductionLotStageEntryService;
import com.khata.product.entity.Product;
import com.khata.product.repositories.ProductRepo;
import com.khata.settings.department.entity.Department;
import com.khata.settings.department.repositories.DepartmentRepo;
import com.khata.staff.employee.entity.Employee;
import com.khata.staff.employee.repositories.EmployeeRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductionLotStageEntryServiceImpl implements ProductionLotStageEntryService {

    private static final String CUTTING_MASTER_ROLE = "CUTTING_MASTER";
    private static final String PATTERN_MASTER_ROLE = "PATTERN_MASTER";

    private final ProductionLotStageEntryRepo stageEntryRepo;
    private final ProductionLotRepo productionLotRepo;
    private final ProductRepo productRepo;
    private final ProductionStageRepo productionStageRepo;
    private final DepartmentRepo departmentRepo;
    private final EmployeeRepo employeeRepo;
    private final UserService userService;
    private final ProductionStageFlowResolver stageFlowResolver;
    private final ProductionLotProgressService productionLotProgressService;
    private final ProductionLotStageEntryMapper mapper;

    @Override
    @Transactional
    public ProductionLotStageEntryDTO saveOrUpdateEntry(Integer productionLotId, ProductionLotStageEntryDTO entryDTO) {
        Integer currentUserId = userService.getCurrentUserId();
        ProductionLot productionLot = getProductionLot(productionLotId, currentUserId);
        Product product = getAllocatedProduct(productionLot, entryDTO.getProductId(), currentUserId);
        ProductionStage productionStage = getProductionStage(entryDTO.getProductionStageId(), currentUserId);
        Department department = getDepartment(entryDTO.getDepartmentId(), currentUserId);

        normalizeEmployeeAllocationPayload(entryDTO, department != null);
        Employee employee = getActiveEmployee(entryDTO.getEmployeeId(), currentUserId);

        validateStageWorkType(entryDTO, productionStage);
        List<ProductionStage> productFlow = getProductFlow(product, currentUserId);
        validateStageInProductFlow(productionStage, productFlow);
        StageExpectation expectation = getStageExpectation(
                productionLotId,
                product,
                productionStage,
                productFlow,
                currentUserId);
        if (!expectation.firstStage() && !expectation.previousStageCompleted()) {
            throw new BadRequestException("Previous production stage must be completed before saving this stage.");
        }
        validateStageDepartment(department, productionStage);
        validateDepartmentBelongsToProduct(department, product);
        validateEmployeeAssignedToDepartment(employee, department, productionStage, "Employee");
        validateChildRows(entryDTO, productionStage, product, department, currentUserId);

        ProductionLotStageEntry entry = findExistingEntry(productionLotId, entryDTO, currentUserId)
                .orElseGet(ProductionLotStageEntry::new);
        entry.setProductionLot(productionLot);
        entry.setProduct(product);
        entry.setProductionStage(productionStage);
        entry.setDepartment(department);
        entry.setEmployee(employee);
        entry.setWorkType(entryDTO.getWorkType());
        entry.setCompletedPieces(entryDTO.getCompletedPieces());
        entry.setRemarks(entryDTO.getRemarks());
        entry.setCreatedUserId(currentUserId);
        entry.setCompleted(true);

        applyWorkTypeRules(entry, entryDTO, expectation);
        replaceEmployeeRows(entry, entryDTO, currentUserId);
        replaceSizeBreakdownRows(entry, entryDTO);

        ProductionLotStageEntry savedEntry = stageEntryRepo.save(entry);
        applyMasterAdjustment(savedEntry, productFlow, currentUserId);
        syncNextStageExpectedPieces(savedEntry, productFlow, currentUserId);
        productionLotProgressService.updateProgress(productionLot, currentUserId);
        return mapper.toDTO(savedEntry);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductionLotStageEntryContextDTO getContext(
            Integer productionLotId,
            Integer productId,
            Integer productionStageId,
            Integer departmentId) {
        Integer currentUserId = userService.getCurrentUserId();
        ProductionLot productionLot = getProductionLot(productionLotId, currentUserId);
        Product product = getAllocatedProduct(productionLot, productId, currentUserId);
        ProductionStage productionStage = getProductionStage(productionStageId, currentUserId);
        Department department = getDepartment(departmentId, currentUserId);
        List<ProductionStage> productFlow = getProductFlow(product, currentUserId);
        validateStageInProductFlow(productionStage, productFlow);
        validateStageDepartment(department, productionStage);
        validateDepartmentBelongsToProduct(department, product);
        StageExpectation expectation = getStageExpectation(
                productionLotId,
                product,
                productionStage,
                productFlow,
                currentUserId);

        ProductionLotStageEntryContextDTO context = new ProductionLotStageEntryContextDTO();
        context.setProductionLotId(productionLotId);
        context.setProductId(productId);
        context.setCurrentStageId(productionStage.getId());
        context.setCurrentStageName(productionStage.getStageName());
        context.setCurrentWorkType(productionStage.getWorkType());
        Optional<ProductionLotStageEntry> existingEntry = departmentId == null
                ? stageEntryRepo.findFirstByProductionLotIdAndProductIdAndProductionStageIdAndCreatedUserIdOrderByIdDesc(
                        productionLotId,
                        productId,
                        productionStageId,
                        currentUserId)
                : stageEntryRepo.findByProductionLotIdAndProductIdAndProductionStageIdAndDepartmentIdAndCreatedUserId(
                        productionLotId,
                        productId,
                        productionStageId,
                        departmentId,
                        currentUserId);
        context.setExpectedPieces(existingEntry
                .map(ProductionLotStageEntry::getExpectedPieces)
                .orElse(expectation.expectedPieces()));
        context.setPreviousStageId(expectation.previousStage() == null ? null : expectation.previousStage().getId());
        context.setPreviousStageName(expectation.previousStage() == null ? null : expectation.previousStage().getStageName());
        context.setPreviousCompletedPieces(expectation.previousCompletedPieces());
        context.setPreviousStageCompleted(expectation.previousStageCompleted());
        context.setEntry(existingEntry.map(mapper::toDTO).orElse(null));
        return context;
    }

    @Override
    @Transactional(readOnly = true)
    public ProductionLotStageEntryDTO getEntry(
            Integer productionLotId,
            Integer productId,
            Integer productionStageId,
            Integer departmentId,
            ProductionStageWorkType workType) {
        Integer currentUserId = userService.getCurrentUserId();
        getProductionLot(productionLotId, currentUserId);

        Optional<ProductionLotStageEntry> entry;
        if (departmentId != null) {
            entry = stageEntryRepo.findByProductionLotIdAndProductIdAndProductionStageIdAndDepartmentIdAndCreatedUserId(
                    productionLotId,
                    productId,
                    productionStageId,
                    departmentId,
                    currentUserId);
        } else if (workType != null) {
            entry = stageEntryRepo.findByProductionLotIdAndProductIdAndProductionStageIdAndWorkTypeAndDepartmentIsNullAndCreatedUserId(
                    productionLotId,
                    productId,
                    productionStageId,
                    workType,
                    currentUserId);
        } else {
            throw new BadRequestException("departmentId or workType is required to find a stage entry.");
        }
        return entry.map(mapper::toDTO).orElse(null);
    }

    private void normalizeEmployeeAllocationPayload(
            ProductionLotStageEntryDTO entryDTO,
            boolean departmentEntry) {
        if (entryDTO.getEmployees() == null) {
            entryDTO.setEmployees(new ArrayList<>());
        }

        boolean hasEmployees = !entryDTO.getEmployees().isEmpty();
        boolean hasLegacyEmployee = entryDTO.getEmployeeId() != null;
        boolean hasLegacyCompletedPieces = entryDTO.getCompletedPieces() != null;

        if (!departmentEntry) {
            if (!hasLegacyCompletedPieces) {
                throw new BadRequestException("Completed pieces is required.");
            }
            return;
        }

        if (hasEmployees && (hasLegacyEmployee || hasLegacyCompletedPieces)) {
            throw new BadRequestException("Use either employees[] or employeeId + completedPieces, not both.");
        }

        if (hasEmployees) {
            int completedPieces = 0;
            for (ProductionLotStageEntryEmployeeDTO employeeDTO : entryDTO.getEmployees()) {
                if (employeeDTO.getEmployeeId() == null) {
                    throw new BadRequestException("Employee is required.");
                }
                if (employeeDTO.getPieces() == null || employeeDTO.getPieces() <= 0) {
                    throw new BadRequestException("Employee pieces must be greater than 0.");
                }
                if (employeeDTO.getDepartmentId() == null) {
                    employeeDTO.setDepartmentId(entryDTO.getDepartmentId());
                }
                completedPieces += employeeDTO.getPieces();
            }
            entryDTO.setEmployeeId(null);
            entryDTO.setCompletedPieces(completedPieces);
            return;
        }

        if (hasLegacyEmployee || hasLegacyCompletedPieces) {
            if (!hasLegacyEmployee || !hasLegacyCompletedPieces) {
                throw new BadRequestException("Both employeeId and completedPieces are required for legacy department entries.");
            }
            if (entryDTO.getCompletedPieces() <= 0) {
                throw new BadRequestException("Completed pieces must be greater than 0.");
            }
            ProductionLotStageEntryEmployeeDTO employeeDTO = new ProductionLotStageEntryEmployeeDTO();
            employeeDTO.setDepartmentId(entryDTO.getDepartmentId());
            employeeDTO.setEmployeeId(entryDTO.getEmployeeId());
            employeeDTO.setPieces(entryDTO.getCompletedPieces());
            entryDTO.getEmployees().add(employeeDTO);
            return;
        }

        throw new BadRequestException("Employees are required for department-based stage entries.");
    }

    @Override
    @Transactional
    public ProductionLotStageEntryDTO completeEntry(Integer productionLotId, Integer entryId) {
        Integer currentUserId = userService.getCurrentUserId();
        ProductionLotStageEntry entry = stageEntryRepo.findByIdAndProductionLotIdAndCreatedUserId(
                        entryId,
                        productionLotId,
                        currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Production lot stage entry", "id", entryId));

        if (entry.getCompletedPieces() == null || entry.getCompletedPieces() <= 0) {
            throw new BadRequestException("Completed pieces must be greater than 0 before completion.");
        }
        if (entry.getWorkType() == ProductionStageWorkType.CUTTING_PATTERN && entry.getSizeBreakdowns().isEmpty()) {
            throw new BadRequestException("Cutting pattern entry must have size breakdowns before completion.");
        }
        Product product = getAllocatedProduct(entry.getProductionLot(), entry.getProduct().getId(), currentUserId);
        ProductionStage productionStage = getProductionStage(entry.getProductionStage().getId(), currentUserId);
        StageExpectation expectation = getStageExpectation(
                productionLotId,
                product,
                productionStage,
                getProductFlow(product, currentUserId),
                currentUserId);
        if (!expectation.firstStage() && !expectation.previousStageCompleted()) {
            throw new BadRequestException("Previous production stage must be completed before completing this stage.");
        }
        entry.setCompleted(true);
        ProductionLotStageEntry savedEntry = stageEntryRepo.save(entry);
        productionLotProgressService.updateProgress(savedEntry.getProductionLot(), currentUserId);
        return mapper.toDTO(savedEntry);
    }

    private Optional<ProductionLotStageEntry> findExistingEntry(
            Integer productionLotId,
            ProductionLotStageEntryDTO entryDTO,
            Integer currentUserId) {
        if (entryDTO.getDepartmentId() != null) {
            return stageEntryRepo.findByProductionLotIdAndProductIdAndProductionStageIdAndDepartmentIdAndCreatedUserId(
                    productionLotId,
                    entryDTO.getProductId(),
                    entryDTO.getProductionStageId(),
                    entryDTO.getDepartmentId(),
                    currentUserId);
        }
        return stageEntryRepo.findByProductionLotIdAndProductIdAndProductionStageIdAndWorkTypeAndDepartmentIsNullAndCreatedUserId(
                productionLotId,
                entryDTO.getProductId(),
                entryDTO.getProductionStageId(),
                entryDTO.getWorkType(),
                currentUserId);
    }

    private void applyWorkTypeRules(
            ProductionLotStageEntry entry,
            ProductionLotStageEntryDTO entryDTO,
            StageExpectation expectation) {
        if (entryDTO.getWorkType() == ProductionStageWorkType.CUTTING_PATTERN) {
            validateCuttingPattern(entryDTO);
        }

        entry.setExpectedPieces(resolveExpectedPieces(entry, entryDTO, expectation));
        applyPieceDifference(entry, entryDTO);
    }

    private Integer resolveExpectedPieces(
            ProductionLotStageEntry entry,
            ProductionLotStageEntryDTO entryDTO,
            StageExpectation expectation) {
        if (entry.getId() != null && entry.getExpectedPieces() != null) {
            return entry.getExpectedPieces();
        }
        if (!expectation.firstStage()) {
            return expectation.expectedPieces();
        }
        return entryDTO.getCompletedPieces();
    }

    private boolean isMasterAdjustmentRequested(ProductionLotStageEntryDTO entryDTO) {
        return Boolean.TRUE.equals(entryDTO.getAddToMaster())
                || Boolean.TRUE.equals(entryDTO.getDeductFromMaster());
    }

    private void validateCuttingPattern(ProductionLotStageEntryDTO entryDTO) {
        if (entryDTO.getEmployees().isEmpty()) {
            throw new BadRequestException("Cutting pattern entry must include employees.");
        }

        Set<String> roleCodes = entryDTO.getEmployees().stream()
                .map(ProductionLotStageEntryEmployeeDTO::getRoleCode)
                .filter(roleCode -> roleCode != null && !roleCode.isBlank())
                .map(roleCode -> roleCode.trim().toUpperCase())
                .collect(Collectors.toSet());
        if (!roleCodes.contains(CUTTING_MASTER_ROLE) || !roleCodes.contains(PATTERN_MASTER_ROLE)) {
            throw new BadRequestException("Cutting pattern entry must include Cutting Master and Pattern Master employees.");
        }

        if (entryDTO.getSizeBreakdowns().isEmpty()) {
            throw new BadRequestException("At least one size breakdown is required.");
        }
        int totalSizePieces = entryDTO.getSizeBreakdowns().stream()
                .mapToInt(ProductionLotStageSizeBreakdownDTO::getPieces)
                .sum();
        if (!entryDTO.getCompletedPieces().equals(totalSizePieces)) {
            throw new BadRequestException("Completed pieces must equal the sum of size breakdown pieces.");
        }
    }

    private void applyPieceDifference(ProductionLotStageEntry entry, ProductionLotStageEntryDTO entryDTO) {
        Integer expectedPiecesValue = entry.getExpectedPieces();
        if (expectedPiecesValue == null || expectedPiecesValue <= 0) {
            entry.setExpectedPieces(expectedPiecesValue);
            entry.setExtraPieces(0);
            entry.setDamagePieces(0);
            entry.setAddToMaster(false);
            entry.setDeductFromMaster(false);
            return;
        }

        int expectedPieces = expectedPiecesValue;
        int completedPieces = entryDTO.getCompletedPieces();

        if (completedPieces > expectedPieces) {
            if (Boolean.TRUE.equals(entryDTO.getDeductFromMaster())) {
                throw new BadRequestException("Deduct from master is allowed only when completed pieces are less than expected pieces.");
            }
            entry.setExtraPieces(completedPieces - expectedPieces);
            entry.setDamagePieces(0);
            entry.setAddToMaster(Boolean.TRUE.equals(entryDTO.getAddToMaster()));
            entry.setDeductFromMaster(false);
            return;
        }

        if (completedPieces < expectedPieces) {
            if (Boolean.TRUE.equals(entryDTO.getAddToMaster())) {
                throw new BadRequestException("Add to master is allowed only when completed pieces are greater than expected pieces.");
            }
            entry.setExtraPieces(0);
            entry.setDamagePieces(expectedPieces - completedPieces);
            entry.setAddToMaster(false);
            entry.setDeductFromMaster(Boolean.TRUE.equals(entryDTO.getDeductFromMaster()));
            return;
        }

        entry.setExtraPieces(0);
        entry.setDamagePieces(0);
        entry.setAddToMaster(false);
        entry.setDeductFromMaster(false);
    }

    private void applyMasterAdjustment(
            ProductionLotStageEntry sourceEntry,
            List<ProductionStage> productFlow,
            Integer currentUserId) {
        if (isFirstStageEntry(sourceEntry, productFlow)) {
            return;
        }
        if (sourceEntry.getCompletedPieces() == null) {
            return;
        }

        ProductionStage firstStage = productFlow.get(0);
        ProductionLotStageEntry masterEntry = stageEntryRepo
                .findByProductionLotIdAndProductIdAndProductionStageIdAndCreatedUserIdOrderByIdAsc(
                        sourceEntry.getProductionLot().getId(),
                        sourceEntry.getProduct().getId(),
                        firstStage.getId(),
                        currentUserId)
                .stream()
                .filter(entry -> entry.getDepartment() == null)
                .findFirst()
                .orElseThrow(() -> new BadRequestException("First production stage entry must exist before applying master adjustment."));

        if (!isMasterAdjustmentRequested(sourceEntry)) {
            resetMasterBaseline(masterEntry);
            stageEntryRepo.save(masterEntry);
            return;
        }

        masterEntry.setCompletedPieces(sourceEntry.getCompletedPieces());
        recalculateMasterPieceDifference(masterEntry);
        stageEntryRepo.save(masterEntry);
    }

    private boolean isFirstStageEntry(ProductionLotStageEntry sourceEntry, List<ProductionStage> productFlow) {
        return productFlow.isEmpty()
                || productFlow.get(0).getId().equals(sourceEntry.getProductionStage().getId());
    }

    private boolean isMasterAdjustmentRequested(ProductionLotStageEntry entry) {
        return Boolean.TRUE.equals(entry.getAddToMaster())
                || Boolean.TRUE.equals(entry.getDeductFromMaster());
    }

    private void resetMasterBaseline(ProductionLotStageEntry masterEntry) {
        if (masterEntry.getExpectedPieces() == null) {
            throw new BadRequestException("First production stage expected pieces must exist before resetting master baseline.");
        }
        masterEntry.setCompletedPieces(masterEntry.getExpectedPieces());
        masterEntry.setExtraPieces(0);
        masterEntry.setDamagePieces(0);
        masterEntry.setAddToMaster(false);
        masterEntry.setDeductFromMaster(false);
    }

    private void recalculateMasterPieceDifference(ProductionLotStageEntry masterEntry) {
        Integer expectedPieces = masterEntry.getExpectedPieces();
        Integer completedPieces = masterEntry.getCompletedPieces();
        if (expectedPieces == null || completedPieces == null || expectedPieces <= 0) {
            masterEntry.setExtraPieces(0);
            masterEntry.setDamagePieces(0);
            return;
        }

        if (completedPieces > expectedPieces) {
            masterEntry.setExtraPieces(completedPieces - expectedPieces);
            masterEntry.setDamagePieces(0);
            return;
        }
        if (completedPieces < expectedPieces) {
            masterEntry.setExtraPieces(0);
            masterEntry.setDamagePieces(expectedPieces - completedPieces);
            return;
        }

        masterEntry.setExtraPieces(0);
        masterEntry.setDamagePieces(0);
    }

    private void syncNextStageExpectedPieces(
            ProductionLotStageEntry sourceEntry,
            List<ProductionStage> productFlow,
            Integer currentUserId) {
        if (sourceEntry.getCompletedPieces() == null) {
            return;
        }

        getNextStage(sourceEntry.getProductionStage(), productFlow)
                .ifPresent(nextStage -> {
                    List<ProductionLotStageEntry> nextStageEntries = stageEntryRepo
                            .findByProductionLotIdAndProductIdAndProductionStageIdAndCreatedUserIdOrderByIdAsc(
                                    sourceEntry.getProductionLot().getId(),
                                    sourceEntry.getProduct().getId(),
                                    nextStage.getId(),
                                    currentUserId);
                    nextStageEntries.forEach(entry -> {
                        entry.setExpectedPieces(sourceEntry.getCompletedPieces());
                        recalculateExistingEntryPieceDifference(entry);
                    });
                    stageEntryRepo.saveAll(nextStageEntries);
                });
    }

    private Optional<ProductionStage> getNextStage(ProductionStage currentStage, List<ProductionStage> productFlow) {
        for (int index = 0; index < productFlow.size() - 1; index++) {
            if (productFlow.get(index).getId().equals(currentStage.getId())) {
                return Optional.of(productFlow.get(index + 1));
            }
        }
        return Optional.empty();
    }

    private void recalculateExistingEntryPieceDifference(ProductionLotStageEntry entry) {
        Integer expectedPieces = entry.getExpectedPieces();
        Integer completedPieces = entry.getCompletedPieces();
        if (expectedPieces == null || completedPieces == null || expectedPieces <= 0) {
            entry.setExtraPieces(0);
            entry.setDamagePieces(0);
            entry.setAddToMaster(false);
            entry.setDeductFromMaster(false);
            return;
        }

        if (completedPieces > expectedPieces) {
            entry.setExtraPieces(completedPieces - expectedPieces);
            entry.setDamagePieces(0);
            entry.setDeductFromMaster(false);
            return;
        }
        if (completedPieces < expectedPieces) {
            entry.setExtraPieces(0);
            entry.setDamagePieces(expectedPieces - completedPieces);
            entry.setAddToMaster(false);
            return;
        }

        entry.setExtraPieces(0);
        entry.setDamagePieces(0);
        entry.setAddToMaster(false);
        entry.setDeductFromMaster(false);
    }

    private void replaceEmployeeRows(
            ProductionLotStageEntry entry,
            ProductionLotStageEntryDTO entryDTO,
            Integer currentUserId) {
        entry.getEmployees().clear();
        for (ProductionLotStageEntryEmployeeDTO employeeDTO : entryDTO.getEmployees()) {
            ProductionLotStageEntryEmployee employeeEntry = new ProductionLotStageEntryEmployee();
            employeeEntry.setStageEntry(entry);
            employeeEntry.setDepartment(employeeDTO.getDepartmentId() == null
                    ? entry.getDepartment()
                    : getDepartment(employeeDTO.getDepartmentId(), currentUserId));
            employeeEntry.setEmployee(getActiveEmployee(employeeDTO.getEmployeeId(), currentUserId));
            employeeEntry.setRoleCode(normalizeRoleCode(employeeDTO.getRoleCode()));
            employeeEntry.setRoleName(employeeDTO.getRoleName());
            employeeEntry.setPieces(employeeDTO.getPieces());
            entry.getEmployees().add(employeeEntry);
        }
    }

    private void replaceSizeBreakdownRows(
            ProductionLotStageEntry entry,
            ProductionLotStageEntryDTO entryDTO) {
        entry.getSizeBreakdowns().clear();
        for (ProductionLotStageSizeBreakdownDTO sizeBreakdownDTO : entryDTO.getSizeBreakdowns()) {
            ProductionLotStageSizeBreakdown sizeBreakdown = new ProductionLotStageSizeBreakdown();
            sizeBreakdown.setStageEntry(entry);
            sizeBreakdown.setSize(sizeBreakdownDTO.getSize().trim());
            sizeBreakdown.setPieces(sizeBreakdownDTO.getPieces());
            entry.getSizeBreakdowns().add(sizeBreakdown);
        }
    }

    private void validateChildRows(
            ProductionLotStageEntryDTO entryDTO,
            ProductionStage productionStage,
            Product product,
            Department parentDepartment,
            Integer currentUserId) {
        Set<Integer> employeeIds = new HashSet<>();
        for (ProductionLotStageEntryEmployeeDTO employeeDTO : entryDTO.getEmployees()) {
            if (employeeDTO.getEmployeeId() == null) {
                throw new BadRequestException("Employee is required.");
            }
            if (!employeeIds.add(employeeDTO.getEmployeeId())) {
                throw new BadRequestException("Duplicate employee is not allowed in the same stage entry.");
            }
            Department department = employeeDTO.getDepartmentId() == null
                    ? parentDepartment
                    : getDepartment(employeeDTO.getDepartmentId(), currentUserId);
            Employee employee = getActiveEmployee(employeeDTO.getEmployeeId(), currentUserId);
            validateStageDepartment(department, productionStage);
            validateDepartmentBelongsToProduct(department, product);
            validateEmployeeAssignedToDepartment(employee, department, productionStage, "Employee");
        }
    }

    private void validateStageWorkType(ProductionLotStageEntryDTO entryDTO, ProductionStage productionStage) {
        if (productionStage.getWorkType() != null && productionStage.getWorkType() != entryDTO.getWorkType()) {
            throw new BadRequestException("Work type does not match the selected production stage.");
        }
    }

    private void validateStageDepartment(Department department, ProductionStage productionStage) {
        if (department == null || productionStage.getDepartmentMappings().isEmpty()) {
            return;
        }

        boolean mapped = productionStage.getDepartmentMappings().stream()
                .anyMatch(mapping -> mapping.getDepartment().getId().equals(department.getId()));
        if (!mapped) {
            throw new BadRequestException("Department is not mapped under this production stage.");
        }
    }

    private void validateEmployeeAssignedToDepartment(
            Employee employee,
            Department department,
            ProductionStage productionStage,
            String employeeLabel) {
        if (employee == null) {
            return;
        }

        if (department != null) {
            boolean assignedToDepartment = employee.getDepartmentAssignments().stream()
                    .anyMatch(assignment -> assignment.getDepartment().getId().equals(department.getId()));
            if (!assignedToDepartment) {
                throw new BadRequestException(employeeLabel + " is not assigned to the selected department.");
            }
            return;
        }

        Set<Integer> stageDepartmentIds = productionStage.getDepartmentMappings().stream()
                .map(mapping -> mapping.getDepartment().getId())
                .collect(Collectors.toSet());
        if (!stageDepartmentIds.isEmpty()) {
            boolean assignedToStageDepartment = employee.getDepartmentAssignments().stream()
                    .anyMatch(assignment -> stageDepartmentIds.contains(assignment.getDepartment().getId()));
            if (!assignedToStageDepartment) {
                throw new BadRequestException(employeeLabel + " is not assigned to a department mapped under this stage.");
            }
        }
    }

    private List<ProductionStage> getProductFlow(Product product, Integer currentUserId) {
        List<ProductionStage> productFlow = stageFlowResolver.getConfiguredProductFlow(product, currentUserId);
        if (productFlow.isEmpty()) {
            throw new BadRequestException("Product must have department rates before production stage entries can be saved.");
        }
        return productFlow;
    }

    private void validateStageInProductFlow(ProductionStage productionStage, List<ProductionStage> productFlow) {
        boolean stageInFlow = productFlow.stream()
                .anyMatch(flowStage -> flowStage.getId().equals(productionStage.getId()));
        if (!stageInFlow) {
            throw new BadRequestException("Production stage is not part of this product's configured production flow.");
        }
    }

    private StageExpectation getStageExpectation(
            Integer productionLotId,
            Product product,
            ProductionStage currentStage,
            List<ProductionStage> productFlow,
            Integer currentUserId) {
        int currentStageIndex = -1;
        for (int index = 0; index < productFlow.size(); index++) {
            if (productFlow.get(index).getId().equals(currentStage.getId())) {
                currentStageIndex = index;
                break;
            }
        }
        if (currentStageIndex < 0) {
            throw new BadRequestException("Production stage is not part of this product's configured production flow.");
        }
        if (currentStageIndex == 0) {
            return new StageExpectation(null, null, null, true, true);
        }

        ProductionStage previousStage = productFlow.get(currentStageIndex - 1);
        ProductionLotProgressService.StageCompletion previousCompletion = productionLotProgressService.getStageCompletion(
                productionLotId,
                product.getId(),
                previousStage,
                getProductDepartmentIds(product),
                currentUserId);
        return new StageExpectation(
                previousStage,
                previousCompletion.completedPieces(),
                previousCompletion.completedPieces(),
                previousCompletion.completed(),
                false);
    }

    private Set<Integer> getProductDepartmentIds(Product product) {
        return stageFlowResolver.getProductDepartmentIds(product);
    }

    private void validateDepartmentBelongsToProduct(Department department, Product product) {
        if (department == null) {
            return;
        }
        if (!getProductDepartmentIds(product).contains(department.getId())) {
            throw new BadRequestException("Department is not configured for this product.");
        }
    }

    private ProductionLot getProductionLot(Integer productionLotId, Integer currentUserId) {
        return productionLotRepo.findByIdAndCreatedUserId(productionLotId, currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Production lot", "id", productionLotId));
    }

    private Product getAllocatedProduct(ProductionLot productionLot, Integer productId, Integer currentUserId) {
        Product product = productRepo.findByIdAndCreatedUserId(productId, currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        boolean allocated = productionLot.getAllocations().stream()
                .anyMatch(allocation -> allocation.getProduct().getId().equals(productId));
        if (!allocated) {
            throw new BadRequestException("Product is not allocated to this production lot.");
        }
        return product;
    }

    private ProductionStage getProductionStage(Integer productionStageId, Integer currentUserId) {
        return productionStageRepo.findByIdAndCreatedUserId(productionStageId, currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Production stage", "id", productionStageId));
    }

    private Department getDepartment(Integer departmentId, Integer currentUserId) {
        if (departmentId == null) {
            return null;
        }
        return departmentRepo.findByIdAndCreatedUserId(departmentId, currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", departmentId));
    }

    private Employee getActiveEmployee(Integer employeeId, Integer currentUserId) {
        if (employeeId == null) {
            return null;
        }
        Employee employee = employeeRepo.findByIdAndCreatedUserId(employeeId, currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", employeeId));
        if (!Boolean.TRUE.equals(employee.getActive())) {
            throw new BadRequestException("Employee must be active.");
        }
        return employee;
    }

    private String normalizeRoleCode(String roleCode) {
        if (roleCode == null) {
            return null;
        }
        return roleCode.trim().toUpperCase();
    }

    private record StageExpectation(
            ProductionStage previousStage,
            Integer expectedPieces,
            Integer previousCompletedPieces,
            boolean previousStageCompleted,
            boolean firstStage) {
    }

}
