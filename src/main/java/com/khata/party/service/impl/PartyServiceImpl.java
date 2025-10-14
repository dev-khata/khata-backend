package com.khata.party.service.impl;

import com.khata.exceptions.ResourceAlreadyExistsException;
import com.khata.exceptions.ResourceNotFoundException;
import com.khata.party.dto.PartyDTO;
import com.khata.party.entity.Party;
import com.khata.party.repositories.PartyRepo;
import com.khata.party.service.PartyService;
import com.khata.utils.EmailAndPhoneUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class PartyServiceImpl implements PartyService {

    private final PartyRepo partyRepo;
    private final ModelMapper modelMapper;

    public PartyServiceImpl(PartyRepo partyRepo, ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        this.partyRepo = partyRepo;
    }

    @Override
    @Transactional
    public PartyDTO createParty(PartyDTO partyDTO) {
        Party party = modelMapper.map(partyDTO, Party.class);
        checkEmailIfExists(partyDTO.getEmail());
        checkPhoneNumberIfExists(partyDTO.getPhoneNumber());
        Party savedParty = partyRepo.save(party);
        log.info("Party created with name: {}", partyDTO.getName());
        return modelMapper.map(savedParty, PartyDTO.class);
    }

    @Override
    @Transactional
    public PartyDTO updateParty(PartyDTO partyDTO, Integer partyId) {
        Party party = getPartyEntityById(partyId);

        if (!party.getEmail().equals(partyDTO.getEmail())) {
            checkEmailIfExists(partyDTO.getEmail());
        }

        if (!party.getPhoneNumber().equals(partyDTO.getPhoneNumber())) {
            checkPhoneNumberIfExists(partyDTO.getPhoneNumber());
        }

        party.setName(partyDTO.getName());
        party.setEmail(partyDTO.getEmail());
        party.setPartyType(partyDTO.getPartyType());
        party.setPartyBusinessName(partyDTO.getPartyBusinessName());
        party.setPhoneNumber(partyDTO.getPhoneNumber());
        party.setAddress(partyDTO.getAddress());
        party.setCbf(partyDTO.getCbf());
        party.setOpeningBalance(partyDTO.getOpeningBalance());
        party.setTransactionType(partyDTO.getTransactionType());
        Party updatedParty = partyRepo.save(party);
        log.info("Party updated with ID: {}", partyId);
        return modelMapper.map(updatedParty, PartyDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public PartyDTO getPartyById(Integer partyId) {
        Party party = getPartyEntityById(partyId);
        return modelMapper.map(party, PartyDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PartyDTO> getParties(Pageable pageable) {
        Page<Party> parties = partyRepo.findAll(pageable);
        return parties.map(party -> modelMapper.map(party, PartyDTO.class));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PartyDTO> searchPartyByName(String name, Pageable pageable) {
        Page<Party> parties = partyRepo.findByNameContainingIgnoreCase(name, pageable);
        return parties.map(party -> modelMapper.map(party, PartyDTO.class));
    }

    @Override
    public void deleteParty(Integer partyId) {
        Party party = getPartyEntityById(partyId);
        partyRepo.delete(party);
        log.info("Party deleted with ID: {}", partyId);
    }

    private Party getPartyEntityById(Integer partyId) {
        return partyRepo.findById(partyId).orElseThrow(
                () -> new ResourceNotFoundException("Party", "id", partyId)
        );
    }

    private void checkEmailIfExists(String email) {
        if (!EmailAndPhoneUtil.isValidEmail(email)) {throw new IllegalArgumentException("Invalid email format");}

        if (partyRepo.findByEmail(email).isPresent()) {
            log.error("Email already exists: {}", email);
            throw new ResourceAlreadyExistsException("Email", email);
        }
    }

    private void checkPhoneNumberIfExists(String phoneNumber){
        if(!EmailAndPhoneUtil.isValidPhoneNumber(phoneNumber)){throw new IllegalArgumentException("Invalid phone number format");}

        if(partyRepo.findByPhoneNumber(phoneNumber).isPresent()){
            log.error("Phone number already exists: {}", phoneNumber);
            throw new ResourceAlreadyExistsException("Phone number", phoneNumber);
        }
    }
}