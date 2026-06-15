package com.khata.inventory.rawMaterial.service.impl;

import com.khata.auth.service.UserService;
import com.khata.exceptions.BadRequestException;
import com.khata.exceptions.ResourceNotFoundException;
import com.khata.party.entity.Party;
import com.khata.party.repositories.PartyRepo;
import com.khata.inventory.rawMaterial.dto.RawMaterialStockBatchDTO;
import com.khata.inventory.rawMaterial.entity.RawMaterial;
import com.khata.inventory.rawMaterial.entity.RawMaterialStockBatch;
import com.khata.inventory.rawMaterial.repositories.RawMaterialRepo;
import com.khata.inventory.rawMaterial.repositories.RawMaterialStockBatchRepo;
import com.khata.inventory.rawMaterial.service.RawMaterialStockBatchService;
import com.khata.settings.basicSettings.fiscalYear.entity.FiscalYear;
import com.khata.settings.basicSettings.fiscalYear.services.FiscalYearService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@Slf4j
@RequiredArgsConstructor
public class RawMaterialStockBatchServiceImpl implements RawMaterialStockBatchService {

    private final RawMaterialStockBatchRepo stockBatchRepo;
    private final RawMaterialRepo rawMaterialRepo;
    private final PartyRepo partyRepo;
    private final UserService userService;
    private final FiscalYearService fiscalYearService;

    @Override
    @Transactional
    public RawMaterialStockBatchDTO createRawMaterialStockBatch(Integer rawMaterialId, RawMaterialStockBatchDTO stockBatchDTO) {
        Integer currentUserId = userService.getCurrentUserId();
        RawMaterial rawMaterial = getRawMaterialEntityById(rawMaterialId, currentUserId);
        Party party = getPartyEntityById(stockBatchDTO.getPartyId(), currentUserId);

        RawMaterialStockBatch stockBatch = new RawMaterialStockBatch();
        updateStockBatchFields(stockBatch, stockBatchDTO, rawMaterial, party, currentUserId);

        RawMaterialStockBatch savedStockBatch = stockBatchRepo.save(stockBatch);
        log.info("Raw material stock batch created | batchNumber={}", savedStockBatch.getBatchNumber());
        return mapToDTO(savedStockBatch);
    }

    @Override
    @Transactional
    public RawMaterialStockBatchDTO updateRawMaterialStockBatch(Integer rawMaterialId, RawMaterialStockBatchDTO stockBatchDTO, Integer stockBatchId) {
        Integer currentUserId = userService.getCurrentUserId();
        RawMaterialStockBatch stockBatch = getStockBatchEntityById(stockBatchId, rawMaterialId, currentUserId);
        RawMaterial rawMaterial = getRawMaterialEntityById(rawMaterialId, currentUserId);
        Party party = getPartyEntityById(stockBatchDTO.getPartyId(), currentUserId);

        updateStockBatchFields(stockBatch, stockBatchDTO, rawMaterial, party, currentUserId);

        RawMaterialStockBatch updatedStockBatch = stockBatchRepo.save(stockBatch);
        log.info("Raw material stock batch updated | id={}", stockBatchId);
        return mapToDTO(updatedStockBatch);
    }

    @Override
    @Transactional(readOnly = true)
    public RawMaterialStockBatchDTO getRawMaterialStockBatchById(Integer rawMaterialId, Integer stockBatchId) {
        Integer currentUserId = userService.getCurrentUserId();
        RawMaterialStockBatch stockBatch = getStockBatchEntityById(stockBatchId, rawMaterialId, currentUserId);
        return mapToDTO(stockBatch);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RawMaterialStockBatchDTO> getRawMaterialStockBatchesByRawMaterialId(
            Integer rawMaterialId,
            Integer fiscalYearId,
            Pageable pageable) {
        Integer currentUserId = userService.getCurrentUserId();
        getRawMaterialEntityById(rawMaterialId, currentUserId);
        Integer selectedFiscalYearId = fiscalYearId == null
                ? fiscalYearService.getOrCreateByDate(LocalDate.now()).getId()
                : fiscalYearId;
        Page<RawMaterialStockBatch> stockBatches = stockBatchRepo.findByRawMaterialIdAndCreatedUserIdAndFiscalYearId(
                rawMaterialId, currentUserId, selectedFiscalYearId, pageable);
        return stockBatches.map(this::mapToDTO);
    }

    @Override
    @Transactional
    public void deleteRawMaterialStockBatch(Integer rawMaterialId, Integer stockBatchId) {
        Integer currentUserId = userService.getCurrentUserId();
        RawMaterialStockBatch stockBatch = getStockBatchEntityById(stockBatchId, rawMaterialId, currentUserId);
        stockBatchRepo.delete(stockBatch);
        log.info("Raw material stock batch deleted | id={}", stockBatchId);
    }

    private void updateStockBatchFields(
            RawMaterialStockBatch stockBatch,
            RawMaterialStockBatchDTO stockBatchDTO,
            RawMaterial rawMaterial,
            Party party,
            Integer currentUserId) {
        BigDecimal remainingQuantity = stockBatchDTO.getRemainingQuantity() == null
                ? stockBatchDTO.getTotalQuantity()
                : stockBatchDTO.getRemainingQuantity();

        if (remainingQuantity.compareTo(stockBatchDTO.getTotalQuantity()) > 0) {
            throw new BadRequestException("Remaining quantity cannot be greater than total quantity.");
        }

        stockBatch.setBatchNumber(stockBatchDTO.getBatchNumber());
        stockBatch.setRawMaterial(rawMaterial);
        stockBatch.setParty(party);
        stockBatch.setPurchaseDateNepali(stockBatchDTO.getPurchaseDateNepali());
        stockBatch.setPurchaseDateEnglish(stockBatchDTO.getPurchaseDateEnglish());
        stockBatch.setFiscalYear(fiscalYearService.getOrCreateByDate(stockBatchDTO.getPurchaseDateEnglish()));
        stockBatch.setRollCount(stockBatchDTO.getRollCount());
        stockBatch.setTotalQuantity(stockBatchDTO.getTotalQuantity());
        stockBatch.setRemainingQuantity(remainingQuantity);
        stockBatch.setPurchaseRate(stockBatchDTO.getPurchaseRate());
        stockBatch.setTotalAmount(stockBatchDTO.getTotalQuantity().multiply(stockBatchDTO.getPurchaseRate()));
        stockBatch.setCreatedUserId(currentUserId);
    }

    private RawMaterial getRawMaterialEntityById(Integer rawMaterialId, Integer currentUserId) {
        return rawMaterialRepo.findByIdAndCreatedUserId(rawMaterialId, currentUserId).orElseThrow(
                () -> new ResourceNotFoundException("Raw material", "id", rawMaterialId));
    }

    private RawMaterialStockBatch getStockBatchEntityById(Integer stockBatchId, Integer rawMaterialId, Integer currentUserId) {
        return stockBatchRepo.findByIdAndRawMaterialIdAndCreatedUserId(stockBatchId, rawMaterialId, currentUserId).orElseThrow(
                () -> new ResourceNotFoundException("Raw material stock batch", "id", stockBatchId));
    }

    private Party getPartyEntityById(Integer partyId, Integer currentUserId) {
        Party party = partyRepo.findById(partyId).orElseThrow(
                () -> new ResourceNotFoundException("Party", "id", partyId));

        if (!party.getCreatedUserID().equals(currentUserId)) {
            throw new ResourceNotFoundException("Party", "id", partyId);
        }

        return party;
    }

    private RawMaterialStockBatchDTO mapToDTO(RawMaterialStockBatch stockBatch) {
        RawMaterialStockBatchDTO stockBatchDTO = new RawMaterialStockBatchDTO();
        stockBatchDTO.setId(stockBatch.getId());
        stockBatchDTO.setBatchNumber(stockBatch.getBatchNumber());
        stockBatchDTO.setRawMaterialId(stockBatch.getRawMaterial().getId());
        stockBatchDTO.setPartyId(stockBatch.getParty().getId());
        FiscalYear fiscalYear = stockBatch.getFiscalYear();
        if (fiscalYear != null) {
            stockBatchDTO.setFiscalYearId(fiscalYear.getId());
            stockBatchDTO.setFiscalYearName(fiscalYear.getFiscalYearName());
        }
        stockBatchDTO.setPurchaseDateNepali(stockBatch.getPurchaseDateNepali());
        stockBatchDTO.setPurchaseDateEnglish(stockBatch.getPurchaseDateEnglish());
        stockBatchDTO.setRollCount(stockBatch.getRollCount());
        stockBatchDTO.setTotalQuantity(stockBatch.getTotalQuantity());
        stockBatchDTO.setRemainingQuantity(stockBatch.getRemainingQuantity());
        stockBatchDTO.setPurchaseRate(stockBatch.getPurchaseRate());
        stockBatchDTO.setTotalAmount(stockBatch.getTotalAmount());
        stockBatchDTO.setMaterialCode(stockBatch.getRawMaterial().getMaterialCode());
        stockBatchDTO.setMaterialName(stockBatch.getRawMaterial().getMaterialName());
        stockBatchDTO.setPartyName(stockBatch.getParty().getName());
        return stockBatchDTO;
    }
}
