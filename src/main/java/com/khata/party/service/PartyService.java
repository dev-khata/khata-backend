package com.khata.party.service;

import com.khata.party.dto.PartyDTO;
import com.khata.party.dto.PartyOnboardingDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PartyService {
    PartyDTO createParty(PartyOnboardingDTO partyOnboardingDTO);

    PartyDTO updateParty(PartyDTO PartyDTO, Integer partyId);

    PartyDTO getPartyById(Integer partyId);

    Page<PartyDTO> getParties(Pageable pageable);

    Page<PartyDTO> searchPartyByName(String name, Pageable pageable);

    void deleteParty(Integer partyId);
}
