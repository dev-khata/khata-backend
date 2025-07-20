package com.khata.party.util;

import com.khata.party.entity.Party;
import com.khata.party.entity.enums.PartyType;
import com.khata.party.repositories.PartyRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// TODO: Temporary data loader for testing purposes — remove later
@Configuration
public class AddParty {
    @Bean
    CommandLineRunner commandLineRunner(PartyRepo partyRepo) {
        return args -> {
            boolean alreadyExists = partyRepo.existsByEmail("party1@example.com");

            if (!alreadyExists) {
                for (int i = 1; i <= 100; i++) {
                    String email = "party" + i + "@example.com";

                    // double-check each email if needed
                    if (!partyRepo.existsByEmail(email)) {
                        Party party = new Party();
                        party.setName("Party " + i);
                        party.setEmail(email);
                        party.setPhoneNumber("98000000" + String.format("%02d", i));
                        party.setAddress("Address " + i);
                        party.setPartyBusinessName("Business " + i);
                        party.setPartyType(i % 2 == 0 ? PartyType.CUSTOMER : PartyType.VENDOR);
                        partyRepo.save(party);
                    }
                }
            }
        };
    }
}
