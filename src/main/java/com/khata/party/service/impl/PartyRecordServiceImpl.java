package com.khata.party.service.impl;

import com.khata.auth.service.UserService;
import com.khata.exceptions.ResourceNotFoundException;
import com.khata.party.dto.PartyRecordDTO;
import com.khata.party.dto.PartyRecordPaginationResponse;
import com.khata.party.dto.PartyRecordSummaryDTO;
import com.khata.party.entity.Party;
import com.khata.party.entity.PartyRecord;
import com.khata.party.entity.enums.TransactionType;
import com.khata.party.repositories.PartyRecordRepo;
import com.khata.party.repositories.PartyRepo;
import com.khata.party.service.PartyRecordService;
import com.khata.settings.basicSettings.fiscalYear.entity.FiscalYear;
import com.khata.settings.basicSettings.fiscalYear.services.FiscalYearService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@Slf4j
public class PartyRecordServiceImpl implements PartyRecordService {

    private final PartyRecordRepo partyRecordRepo;
    private final ModelMapper modelMapper;
    private final PartyRepo partyRepo;
    private final FiscalYearService fiscalYearService;
    private final UserService userService;

    public PartyRecordServiceImpl(
            PartyRecordRepo partyRecordRepo,
            ModelMapper modelMapper,
            PartyRepo partyRepo,
            FiscalYearService fiscalYearService,
            UserService userService) {
        this.partyRecordRepo = partyRecordRepo;
        this.modelMapper = modelMapper;
        this.partyRepo = partyRepo;
        this.fiscalYearService = fiscalYearService;
        this.userService = userService;
    }

    @Override
    @Transactional
    public PartyRecordDTO createPartyRecord(PartyRecordDTO partyRecordDTO) {
        Party party = getCurrentUserPartyById(partyRecordDTO.getPartyId());
        PartyRecord partyRecord = modelMapper.map(partyRecordDTO, PartyRecord.class);
        partyRecord.setParty(party);
        partyRecord.setFiscalYear(fiscalYearService.getOrCreateByDate(partyRecordDTO.getEnglishDate()));
        PartyRecord savePartyRecord = partyRecordRepo.save(partyRecord);
        return mapToDTO(savePartyRecord);
    }

    @Override
    public PartyRecordDTO updatePartyRecord(PartyRecordDTO partyRecordDTO, Integer partyRecordId) {
        return null;
    }

    @Override
    public PartyRecordDTO getPartyRecordById(Integer partyRecordId) {
        PartyRecord partyRecord = partyRecordRepo.findById(partyRecordId).orElseThrow(
                () -> new ResourceNotFoundException("Party", "id", partyRecordId)
        );
        return mapToDTO(partyRecord);
    }

    @Override
    public Page<PartyRecordDTO> getPartyRecords(Pageable pageable) {
        Page<PartyRecord> partyRecords = partyRecordRepo.findAll(pageable);
        return partyRecords.map(this::mapToDTO);
    }

    @Override
    public Page<PartyRecordDTO> findBypParticularContainingIgnoreCase(Integer partyId, String particular, Pageable pageable) {
        getCurrentUserPartyById(partyId);
        Page<PartyRecord> partyRecords = partyRecordRepo.findByPartyIdAndParticularContainingIgnoreCase(partyId, particular, pageable);
        return partyRecords.map(this::mapToDTO);
    }

    @Override
    public void deletePartyRecord(Integer partyRecordId) {

    }

    @Override
    public PartyRecordPaginationResponse getPartyRecordsByPartyId(Integer partyId, Integer fiscalYearId, Pageable pageable) {
        Integer currentUserId = userService.getCurrentUserId();
        getPartyEntityByIdAndCreatedUserId(partyId, currentUserId);
        Integer selectedFiscalYearId = fiscalYearId == null
                ? fiscalYearService.getOrCreateByDate(LocalDate.now()).getId()
                : fiscalYearId;
        Page<PartyRecordDTO> partyRecords = partyRecordRepo.findByPartyIdAndCreatedUserIdAndFiscalYearId(
                partyId, currentUserId, selectedFiscalYearId, pageable).map(this::mapToDTO);

        BigDecimal totalDebit = partyRecordRepo.calculateTotalByPartyIdAndCreatedUserIdAndFiscalYearIdAndTransactionType(
                partyId, currentUserId, selectedFiscalYearId, TransactionType.DEBIT);
        BigDecimal totalCredit = partyRecordRepo.calculateTotalByPartyIdAndCreatedUserIdAndFiscalYearIdAndTransactionType(
                partyId, currentUserId, selectedFiscalYearId, TransactionType.CREDIT);
        PartyRecordSummaryDTO summary = new PartyRecordSummaryDTO(
                totalDebit,
                totalCredit,
                totalDebit.subtract(totalCredit));

        return new PartyRecordPaginationResponse(
                partyRecords.getContent(),
                partyRecords.getNumber(),
                partyRecords.getSize(),
                partyRecords.getTotalElements(),
                partyRecords.getTotalPages(),
                summary);
    }

    @Override
    public void createPartyRecordWithOpeningBalance(PartyRecordDTO partyRecordDTO, Party party) {
        PartyRecord partyRecord = modelMapper.map(partyRecordDTO, PartyRecord.class);
        partyRecord.setParticular("Opening Balance");
        partyRecord.setParty(party);
        partyRecord.setFiscalYear(fiscalYearService.getOrCreateByDate(partyRecordDTO.getEnglishDate()));
        log.info("Opening balance created for |  partyId={}", party.getId());
        partyRecordRepo.save(partyRecord);
    }

    private Party getCurrentUserPartyById(Integer partyId) {
        Integer currentUserId = userService.getCurrentUserId();
        return partyRepo.findByIdAndCreatedUserID(partyId, currentUserId).orElseThrow(
                () -> new ResourceNotFoundException("Party", "id", partyId)
        );
    }

    private Party getPartyEntityByIdAndCreatedUserId(Integer partyId, Integer createdUserId) {
        return partyRepo.findByIdAndCreatedUserID(partyId, createdUserId).orElseThrow(
                () -> new ResourceNotFoundException("Party", "id", partyId)
        );
    }

    private PartyRecordDTO mapToDTO(PartyRecord partyRecord) {
        PartyRecordDTO partyRecordDTO = modelMapper.map(partyRecord, PartyRecordDTO.class);
        FiscalYear fiscalYear = partyRecord.getFiscalYear();
        if (fiscalYear != null) {
            partyRecordDTO.setFiscalYearId(fiscalYear.getId());
            partyRecordDTO.setFiscalYearName(fiscalYear.getFiscalYearName());
        }
        return partyRecordDTO;
    }
}
