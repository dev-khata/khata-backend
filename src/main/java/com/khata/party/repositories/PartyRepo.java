package com.khata.party.repositories;

import com.khata.party.entity.Party;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PartyRepo extends JpaRepository<Party, Integer> {
    boolean existsByEmail(String email);
    Optional<Party> findByEmail(String email);
    Optional<Party> findByPhoneNumber(String phoneNumber);
    Page<Party> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
