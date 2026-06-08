package com.khata.inventory.rawMaterial.service.impl;

import com.khata.auth.service.UserService;
import com.khata.exceptions.ResourceAlreadyExistsException;
import com.khata.exceptions.ResourceNotFoundException;
import com.khata.inventory.rawMaterial.dto.RawMaterialDTO;
import com.khata.inventory.rawMaterial.entity.RawMaterial;
import com.khata.inventory.rawMaterial.repositories.RawMaterialRepo;
import com.khata.inventory.rawMaterial.repositories.RawMaterialStockBatchRepo;
import com.khata.inventory.rawMaterial.repositories.projection.RawMaterialRollCountProjection;
import com.khata.inventory.rawMaterial.service.RawMaterialService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RawMaterialServiceImpl implements RawMaterialService {

    private final RawMaterialRepo rawMaterialRepo;
    private final RawMaterialStockBatchRepo rawMaterialStockBatchRepo;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public RawMaterialDTO createRawMaterial(RawMaterialDTO rawMaterialDTO) {
        Integer currentUserId = userService.getCurrentUserId();
        checkMaterialCodeIfExists(rawMaterialDTO.getMaterialCode(), currentUserId);

        RawMaterial rawMaterial = modelMapper.map(rawMaterialDTO, RawMaterial.class);
        rawMaterial.setCreatedUserId(currentUserId);

        RawMaterial savedRawMaterial = rawMaterialRepo.save(rawMaterial);
        log.info("Raw material created | materialCode={}", savedRawMaterial.getMaterialCode());
        return mapToDTO(savedRawMaterial);
    }

    @Override
    @Transactional
    public RawMaterialDTO updateRawMaterial(RawMaterialDTO rawMaterialDTO, Integer rawMaterialId) {
        Integer currentUserId = userService.getCurrentUserId();
        RawMaterial rawMaterial = getRawMaterialEntityById(rawMaterialId, currentUserId);

        if (!rawMaterial.getMaterialCode().equals(rawMaterialDTO.getMaterialCode())) {
            checkMaterialCodeIfExists(rawMaterialDTO.getMaterialCode(), currentUserId);
        }

        rawMaterial.setMaterialCode(rawMaterialDTO.getMaterialCode());
        rawMaterial.setMaterialName(rawMaterialDTO.getMaterialName());
        rawMaterial.setUnit(rawMaterialDTO.getUnit());
        rawMaterial.setActive(rawMaterialDTO.isActive());

        RawMaterial updatedRawMaterial = rawMaterialRepo.save(rawMaterial);
        log.info("Raw material updated | id={}", rawMaterialId);
        return mapToDTO(updatedRawMaterial);
    }

    @Override
    @Transactional(readOnly = true)
    public RawMaterialDTO getRawMaterialById(Integer rawMaterialId) {
        Integer currentUserId = userService.getCurrentUserId();
        RawMaterial rawMaterial = getRawMaterialEntityById(rawMaterialId, currentUserId);
        return mapToDTO(rawMaterial);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RawMaterialDTO> getRawMaterials(Pageable pageable) {
        Integer currentUserId = userService.getCurrentUserId();
        Page<RawMaterial> rawMaterials = rawMaterialRepo.findByCreatedUserId(currentUserId, pageable);
        return mapToDTOPage(rawMaterials, currentUserId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RawMaterialDTO> searchRawMaterialByName(String name, Pageable pageable) {
        Integer currentUserId = userService.getCurrentUserId();
        Page<RawMaterial> rawMaterials = rawMaterialRepo.findByMaterialNameContainingIgnoreCaseAndCreatedUserId(name, currentUserId, pageable);
        return mapToDTOPage(rawMaterials, currentUserId);
    }

    @Override
    @Transactional
    public void deleteRawMaterial(Integer rawMaterialId) {
        Integer currentUserId = userService.getCurrentUserId();
        RawMaterial rawMaterial = getRawMaterialEntityById(rawMaterialId, currentUserId);
        rawMaterialRepo.delete(rawMaterial);
        log.info("Raw material deleted | id={}", rawMaterialId);
    }

    private RawMaterial getRawMaterialEntityById(Integer rawMaterialId, Integer currentUserId) {
        return rawMaterialRepo.findByIdAndCreatedUserId(rawMaterialId, currentUserId).orElseThrow(
                () -> new ResourceNotFoundException("Raw material", "id", rawMaterialId));
    }

    private void checkMaterialCodeIfExists(String materialCode, Integer currentUserId) {
        if (rawMaterialRepo.findByMaterialCodeAndCreatedUserId(materialCode, currentUserId).isPresent()) {
            log.error("Raw material code already exists: {}", materialCode);
            throw new ResourceAlreadyExistsException("Raw material code", materialCode);
        }
    }

    private RawMaterialDTO mapToDTO(RawMaterial rawMaterial) {
        Integer currentUserId = userService.getCurrentUserId();
        Long totalRollCount = rawMaterialStockBatchRepo.sumRollCountByRawMaterialIdAndCreatedUserId(rawMaterial.getId(), currentUserId);
        return mapToDTO(rawMaterial, totalRollCount);
    }

    private Page<RawMaterialDTO> mapToDTOPage(Page<RawMaterial> rawMaterials, Integer currentUserId) {
        List<Integer> rawMaterialIds = rawMaterials.getContent().stream()
                .map(RawMaterial::getId)
                .toList();

        Map<Integer, Long> rollCountsByRawMaterialId = rawMaterialIds.isEmpty()
                ? Map.of()
                : rawMaterialStockBatchRepo.sumRollCountsByRawMaterialIdsAndCreatedUserId(rawMaterialIds, currentUserId).stream()
                .collect(Collectors.toMap(
                        RawMaterialRollCountProjection::getRawMaterialId,
                        RawMaterialRollCountProjection::getTotalRollCount));

        return rawMaterials.map(rawMaterial -> mapToDTO(rawMaterial, rollCountsByRawMaterialId.get(rawMaterial.getId())));
    }

    private RawMaterialDTO mapToDTO(RawMaterial rawMaterial, Long totalRollCount) {
        RawMaterialDTO rawMaterialDTO = modelMapper.map(rawMaterial, RawMaterialDTO.class);
        rawMaterialDTO.setTotalRollCount(totalRollCount == null ? 0 : totalRollCount.intValue());
        return rawMaterialDTO;
    }
}
