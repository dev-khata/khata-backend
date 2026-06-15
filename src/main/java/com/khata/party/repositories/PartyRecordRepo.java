package com.khata.party.repositories;

import com.khata.party.entity.PartyRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartyRecordRepo extends JpaRepository<PartyRecord, Integer> {
    Page<PartyRecord> findByPartyIdAndParticularContainingIgnoreCase(Integer partyId, String particular, Pageable pageable);

    Page<PartyRecord> findByPartyId(Integer partyId, Pageable pageable);

    Page<PartyRecord> findByPartyIdAndFiscalYearId(Integer partyId, Integer fiscalYearId, Pageable pageable);
}
