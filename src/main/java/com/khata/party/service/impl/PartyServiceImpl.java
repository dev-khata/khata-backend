package com.khata.party.service.impl;

import com.khata.auth.service.AuthService;
import com.khata.auth.service.UserService;
import com.khata.exceptions.ResourceAlreadyExistsException;
import com.khata.exceptions.ResourceNotFoundException;
import com.khata.party.dto.PartyDTO;
import com.khata.party.dto.PartyOnboardingDTO;
import com.khata.party.dto.PartyRecordDTO;
import com.khata.party.entity.Party;
import com.khata.party.repositories.PartyRepo;
import com.khata.party.service.PartyRecordService;
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
    private final PartyRecordService partyRecordService;
    private final AuthService authService;
    private final UserService userService;

    public PartyServiceImpl(PartyRepo partyRepo, ModelMapper modelMapper, PartyRecordService partyRecordService, AuthService authService, UserService userService) {
        this.modelMapper = modelMapper;
        this.partyRepo = partyRepo;
        this.partyRecordService = partyRecordService;
        this.authService = authService;
        this.userService = userService;
    }

    @Override
    @Transactional
    public PartyDTO createParty(PartyOnboardingDTO partyOnboardingDTO) {
        PartyDTO partyDTO = partyOnboardingDTO.getPartyDetails();
        PartyRecordDTO partyRecordDTO = partyOnboardingDTO.getPartyRecords();
        Party party = modelMapper.map(partyDTO, Party.class);
        checkEmailIfExists(partyDTO.getEmail());
        checkPhoneNumberIfExists(partyDTO.getPhoneNumber());
        party.setCreatedUserID(this.userService.getCurrentUserId());
        Party savedParty = partyRepo.save(party);
        log.info("Party created successfully | name={}", savedParty.getName());
        partyRecordService.createPartyRecordWithOpeningBalance(partyRecordDTO, savedParty);
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
        Integer currentUserId = userService.getCurrentUserId();
        Page<Party> parties = partyRepo.findByCreatedUserID(currentUserId, pageable);
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
        Integer currentUserId = this.userService.getCurrentUserId();
        if (!EmailAndPhoneUtil.isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format");
        }

        if (partyRepo.findByEmailAndCreatedUserID(email, currentUserId).isPresent()) {

            log.error("Email already exists: {}", email);
            throw new ResourceAlreadyExistsException("Email", email);
        }
    }

    private void checkPhoneNumberIfExists(String phoneNumber) {
        Integer currentUserId = this.userService.getCurrentUserId();
        if (!EmailAndPhoneUtil.isValidPhoneNumber(phoneNumber)) {
            throw new IllegalArgumentException("Invalid phone number format");
        }

        if (partyRepo.findByPhoneNumberAndCreatedUserID(phoneNumber, currentUserId).isPresent()) {
            log.error("Phone number already exists: {}", phoneNumber);
            throw new ResourceAlreadyExistsException("Phone number", phoneNumber);
        }
    }
}