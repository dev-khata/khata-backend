package com.khata.party.repositories;

import com.khata.party.entity.Party;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PartyRepo extends JpaRepository<Party, Integer> {
    boolean existsByEmail(String email);

    Optional<Party> findByEmailAndCreatedUserID(String email, Integer createdUserID);

    Optional<Party> findByPhoneNumberAndCreatedUserID(String phoneNumber, Integer createdUserID);

    Page<Party> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Party> findByCreatedUserID(Integer createdUserID, Pageable pageable);
}
