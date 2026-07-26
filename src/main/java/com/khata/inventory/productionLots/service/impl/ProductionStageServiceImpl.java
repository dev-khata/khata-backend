package com.khata.inventory.productionLots.service.impl;

import com.khata.auth.service.UserService;
import com.khata.exceptions.BadRequestException;
import com.khata.exceptions.ResourceAlreadyExistsException;
import com.khata.exceptions.ResourceNotFoundException;
import com.khata.inventory.productionLots.dto.ProductionStageDTO;
import com.khata.inventory.productionLots.dto.ProductionStageDepartmentMappingDTO;
import com.khata.inventory.productionLots.dto.ProductionStageWorkTypeOptionDTO;
import com.khata.inventory.productionLots.entity.ProductionStage;
import com.khata.inventory.productionLots.entity.ProductionStageDepartmentMapping;
import com.khata.inventory.productionLots.entity.enums.ProductionStageWorkType;
import com.khata.inventory.productionLots.repositories.ProductionLotRepo;
import com.khata.inventory.productionLots.repositories.ProductionStageRepo;
import com.khata.inventory.productionLots.service.ProductionStageService;
import com.khata.settings.department.entity.Department;
import com.khata.settings.department.repositories.DepartmentRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProductionStageServiceImpl implements ProductionStageService {

    private final ProductionStageRepo productionStageRepo;
    private final ProductionLotRepo productionLotRepo;
    private final DepartmentRepo departmentRepo;
    private final UserService userService;

    @Override
    @Transactional
    public List<ProductionStageDTO> getProductionStages() {
        Integer currentUserId = userService.getCurrentUserId();
        return productionStageRepo.findByCreatedUserIdOrderByDisplayOrderAscIdAsc(currentUserId).stream()
                .map(this::toProductionStageDTO)
                .toList();
    }

    @Override
    public List<ProductionStageWorkTypeOptionDTO> getProductionStageWorkTypes() {
        return List.of(ProductionStageWorkType.values()).stream()
                .map(workType -> new ProductionStageWorkTypeOptionDTO(workType.getLabel(), workType.name()))
                .toList();
    }

    @Override
    @Transactional
    public ProductionStageDTO createProductionStage(ProductionStageDTO productionStageDTO) {
        Integer currentUserId = userService.getCurrentUserId();
        String stageCode = normalizeStageCode(productionStageDTO);
        validateStageCodeIsUnique(stageCode, null, currentUserId);
        validateStageDisplayOrderIsUnique(productionStageDTO.getDisplayOrder(), null, currentUserId);

        ProductionStage productionStage = new ProductionStage();
        productionStage.setCreatedUserId(currentUserId);
        setProductionStageFields(productionStage, productionStageDTO, stageCode);
        replaceDepartments(productionStage, productionStageDTO.getDepartmentIds(), currentUserId);

        return toProductionStageDTO(productionStageRepo.save(productionStage));
    }

    @Override
    @Transactional
    public ProductionStageDTO updateProductionStage(Integer stageId, ProductionStageDTO productionStageDTO) {
        Integer currentUserId = userService.getCurrentUserId();
        ProductionStage productionStage = getProductionStageEntityById(stageId, currentUserId);
        validateStageDisplayOrderIsUnique(productionStageDTO.getDisplayOrder(), stageId, currentUserId);

        setProductionStageFields(productionStage, productionStageDTO);
        replaceDepartments(productionStage, productionStageDTO.getDepartmentIds(), currentUserId);
        return toProductionStageDTO(productionStageRepo.save(productionStage));
    }

    @Override
    @Transactional
    public void deleteProductionStage(Integer stageId) {
        Integer currentUserId = userService.getCurrentUserId();
        ProductionStage productionStage = getProductionStageEntityById(stageId, currentUserId);
        validateProductionStageIsNotUsed(productionStage, currentUserId);
        productionStageRepo.delete(productionStage);
    }

    @Override
    @Transactional
    public ProductionStageDTO updateDepartments(
            Integer stageId,
            List<ProductionStageDepartmentMappingDTO> departmentDTOs) {
        Integer currentUserId = userService.getCurrentUserId();
        ProductionStage productionStage = getProductionStageEntityById(stageId, currentUserId);
        replaceDepartments(
                productionStage,
                getDepartmentIds(departmentDTOs),
                currentUserId);

        return toProductionStageDTO(productionStageRepo.save(productionStage));
    }

    private void setProductionStageFields(
            ProductionStage productionStage,
            ProductionStageDTO productionStageDTO,
            String stageCode) {
        productionStage.setStageCode(stageCode);
        setProductionStageFields(productionStage, productionStageDTO);
    }

    private void setProductionStageFields(
            ProductionStage productionStage,
            ProductionStageDTO productionStageDTO) {
        productionStage.setStageName(productionStageDTO.getStageName());
        productionStage.setDisplayOrder(productionStageDTO.getDisplayOrder());
        productionStage.setWorkType(productionStageDTO.getWorkType());
    }

    private ProductionStage getProductionStageEntityById(Integer stageId, Integer currentUserId) {
        return productionStageRepo.findByIdAndCreatedUserId(stageId, currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Production stage", "id", stageId));
    }

    private void validateStageCodeIsUnique(String stageCode, Integer stageId, Integer currentUserId) {
        productionStageRepo.findByStageCodeAndCreatedUserId(stageCode, currentUserId).ifPresent(existingStage -> {
            if (!existingStage.getId().equals(stageId)) {
                throw new ResourceAlreadyExistsException("Production stage code", stageCode);
            }
        });
    }

    private void validateStageDisplayOrderIsUnique(Integer displayOrder, Integer stageId, Integer currentUserId) {
        productionStageRepo.findByDisplayOrderAndCreatedUserId(displayOrder, currentUserId).ifPresent(existingStage -> {
            if (!existingStage.getId().equals(stageId)) {
                throw new ResourceAlreadyExistsException("Production stage display order", displayOrder);
            }
        });
    }

    private void replaceDepartments(
            ProductionStage productionStage,
            List<Integer> departmentIds,
            Integer currentUserId) {
        Map<Integer, Department> departmentsById = validateDepartmentIds(departmentIds, currentUserId);
        productionStage.getDepartmentMappings().clear();
        if (productionStage.getId() != null) {
            productionStageRepo.saveAndFlush(productionStage);
        }
        departmentIds.forEach(departmentId -> productionStage.getDepartmentMappings().add(
                buildDepartmentMapping(productionStage, departmentId, departmentsById, currentUserId)));
    }

    private List<Integer> getDepartmentIds(List<ProductionStageDepartmentMappingDTO> departmentDTOs) {
        if (departmentDTOs == null || departmentDTOs.isEmpty()) {
            throw new BadRequestException("At least one department is required.");
        }
        return departmentDTOs.stream()
                .map(ProductionStageDepartmentMappingDTO::getDepartmentId)
                .toList();
    }

    private Map<Integer, Department> validateDepartmentIds(List<Integer> requestedDepartmentIds, Integer currentUserId) {
        if (requestedDepartmentIds == null || requestedDepartmentIds.isEmpty()) {
            throw new BadRequestException("At least one department is required.");
        }
        Set<Integer> uniqueDepartmentIds = new HashSet<>();
        Map<Integer, Department> departmentsById = new HashMap<>();
        for (Integer departmentId : requestedDepartmentIds) {
            if (!uniqueDepartmentIds.add(departmentId)) {
                throw new ResourceAlreadyExistsException(
                        "Production stage department",
                        departmentId);
            }
            Department department = departmentRepo.findByIdAndCreatedUserId(departmentId, currentUserId)
                    .orElseThrow(() -> new BadRequestException(
                            "Department does not exist for the logged-in user/company."));
            departmentsById.put(department.getId(), department);
        }
        return departmentsById;
    }

    private ProductionStageDepartmentMapping buildDepartmentMapping(
            ProductionStage productionStage,
            Integer departmentId,
            Map<Integer, Department> departmentsById,
            Integer currentUserId) {
        ProductionStageDepartmentMapping mapping = new ProductionStageDepartmentMapping();
        mapping.setProductionStage(productionStage);
        mapping.setDepartment(departmentsById.get(departmentId));
        mapping.setCreatedUserId(currentUserId);
        return mapping;
    }

    private void validateProductionStageIsNotUsed(ProductionStage productionStage, Integer currentUserId) {
        Set<String> currentStageValues = new HashSet<>();
        currentStageValues.add(productionStage.getStageCode());
        currentStageValues.add(productionStage.getStageName());
        productionStage.getDepartmentMappings().forEach(mapping -> {
            currentStageValues.add(mapping.getDepartment().getDepartmentCode());
            currentStageValues.add(mapping.getDepartment().getDepartmentName());
        });
        if (productionLotRepo.existsByCreatedUserIdAndCurrentStageIn(currentUserId, currentStageValues)
                || productionLotRepo.existsByCreatedUserIdAndAllocations_CurrentStage_Id(
                        currentUserId,
                        productionStage.getId())) {
            throw new BadRequestException("Production stage is used in production lots and cannot be deleted.");
        }
    }

    private String normalizeCode(String code) {
        String normalizedCode = code.trim()
                .replaceAll("[^A-Za-z0-9]+", "_")
                .replaceAll("_+", "_")
                .replaceAll("^_|_$", "")
                .toUpperCase();
        if (normalizedCode.isBlank()) {
            throw new BadRequestException("Code must contain at least one letter or number.");
        }
        return normalizedCode;
    }

    private String normalizeStageCode(ProductionStageDTO productionStageDTO) {
        String sourceCode = productionStageDTO.getStageCode();
        if (sourceCode == null || sourceCode.isBlank()) {
            sourceCode = productionStageDTO.getStageName();
        }
        return normalizeCode(sourceCode);
    }

    private ProductionStageDTO toProductionStageDTO(ProductionStage productionStage) {
        ProductionStageDTO productionStageDTO = new ProductionStageDTO();
        productionStageDTO.setId(productionStage.getId());
        productionStageDTO.setStageCode(productionStage.getStageCode());
        productionStageDTO.setStageName(productionStage.getStageName());
        productionStageDTO.setDisplayOrder(productionStage.getDisplayOrder());
        productionStageDTO.setWorkType(productionStage.getWorkType());
        productionStageDTO.setDepartments(productionStage.getDepartmentMappings().stream()
                .map(this::toProductionStageDepartmentMappingDTO)
                .toList());
        return productionStageDTO;
    }

    private ProductionStageDepartmentMappingDTO toProductionStageDepartmentMappingDTO(
            ProductionStageDepartmentMapping mapping) {
        ProductionStageDepartmentMappingDTO mappingDTO = new ProductionStageDepartmentMappingDTO();
        mappingDTO.setId(mapping.getDepartment().getId());
        mappingDTO.setDepartmentName(mapping.getDepartment().getDepartmentName());
        mappingDTO.setDepartmentCode(mapping.getDepartment().getDepartmentCode());
        return mappingDTO;
    }
}
