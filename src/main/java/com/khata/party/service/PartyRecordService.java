package com.khata.party.service;

import com.khata.party.dto.PartyRecordDTO;
import com.khata.party.dto.PartyRecordPaginationResponse;
import com.khata.party.entity.Party;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PartyRecordService {
    PartyRecordDTO createPartyRecord(PartyRecordDTO partyRecordDTO);

    PartyRecordDTO updatePartyRecord(PartyRecordDTO partyRecordDTO, Integer partyRecordId);

    PartyRecordDTO getPartyRecordById(Integer partyRecordId);

    Page<PartyRecordDTO> getPartyRecords(Pageable pageable);

    Page<PartyRecordDTO> findBypParticularContainingIgnoreCase(Integer partyId, String particular, Pageable pageable);

    void deletePartyRecord(Integer partyRecordId);

    PartyRecordPaginationResponse getPartyRecordsByPartyId(Integer partyId, Integer fiscalYearId, Pageable pageable);

    void createPartyRecordWithOpeningBalance(PartyRecordDTO partyRecordDTO, Party party);

}
