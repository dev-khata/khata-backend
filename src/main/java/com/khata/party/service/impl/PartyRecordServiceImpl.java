package com.khata.party.service.impl;

import com.khata.exceptions.ResourceNotFoundException;
import com.khata.party.dto.PartyRecordDTO;
import com.khata.party.entity.Party;
import com.khata.party.entity.PartyRecord;
import com.khata.party.repositories.PartyRecordRepo;
import com.khata.party.repositories.PartyRepo;
import com.khata.party.service.PartyRecordService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class PartyRecordServiceImpl  implements PartyRecordService {

    private final PartyRecordRepo partyRecordRepo;
    private final ModelMapper modelMapper;
    private final PartyRepo partyRepo;

    public PartyRecordServiceImpl(PartyRecordRepo partyRecordRepo, ModelMapper modelMapper, PartyRepo partyRepo) {
        this.partyRecordRepo = partyRecordRepo;
        this.modelMapper = modelMapper;
        this.partyRepo = partyRepo;
    }

    @Override
    @Transactional
    public PartyRecordDTO createPartyRecord(PartyRecordDTO partyRecordDTO) {
        Party party = getPartyEntityById(partyRecordDTO.getPartyId());
        PartyRecord partyRecord = modelMapper.map(partyRecordDTO, PartyRecord.class);
        partyRecord.setParty(party);
        PartyRecord savePartyRecord = partyRecordRepo.save(partyRecord);
        return modelMapper.map(savePartyRecord, PartyRecordDTO.class);
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
        return modelMapper.map(partyRecord, PartyRecordDTO.class);
    }

    @Override
    public Page<PartyRecordDTO> getPartyRecords(Pageable pageable) {
        Page<PartyRecord> partyRecords = partyRecordRepo.findAll(pageable);
        return partyRecords.map(partyRecord -> modelMapper.map(partyRecord, PartyRecordDTO.class));
    }

    @Override
    public Page<PartyRecordDTO> findBypParticularContainingIgnoreCase(Integer partyId, String particular, Pageable pageable) {
        Party party = getPartyEntityById(partyId);
        Page<PartyRecord> partyRecords = partyRecordRepo.findByPartyIdAndParticularContainingIgnoreCase(partyId, particular, pageable);
        return partyRecords.map(partyRecord -> modelMapper.map(partyRecord, PartyRecordDTO.class));
    }

    @Override
    public void deletePartyRecord(Integer partyRecordId) {

    }

    @Override
    public Page<PartyRecordDTO> getPartyRecordsByPartyId(Integer partyId, Pageable pageable) {
        Page<PartyRecord> partyRecords = partyRecordRepo.findByPartyId(partyId, pageable);
        return partyRecords.map(partyRecord -> modelMapper.map(partyRecord, PartyRecordDTO.class));
    }

    private Party getPartyEntityById(Integer partyId) {
        return partyRepo.findById(partyId).orElseThrow(
                () -> new ResourceNotFoundException("Party", "id", partyId)
        );
    }
}
