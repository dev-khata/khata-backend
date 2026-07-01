package com.khata.settings.accountType.repositories;

import com.khata.settings.accountType.entity.AccountType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AccountTypeRepo extends JpaRepository<AccountType, Integer> {
    @Query("""
            select count(accountType) > 0
            from AccountType accountType
            where accountType.name = :name
              and (accountType.createdUserId = :createdUserId or accountType.isSystemDefault = true)
            """)
    boolean existsVisibleByName(
            @Param("name") String name,
            @Param("createdUserId") Integer createdUserId);

    @Query("""
            select accountType
            from AccountType accountType
            where accountType.name = :name
              and (accountType.createdUserId = :createdUserId or accountType.isSystemDefault = true)
            """)
    Optional<AccountType> findVisibleByName(
            @Param("name") String name,
            @Param("createdUserId") Integer createdUserId);

    Optional<AccountType> findByNameAndCreatedUserId(String name, Integer createdUserId);

    @Query("""
            select accountType
            from AccountType accountType
            where accountType.id = :id
              and (accountType.createdUserId = :createdUserId or accountType.isSystemDefault = true)
            """)
    Optional<AccountType> findVisibleById(
            @Param("id") Integer id,
            @Param("createdUserId") Integer createdUserId);

    @Query("""
            select accountType
            from AccountType accountType
            where accountType.createdUserId = :createdUserId or accountType.isSystemDefault = true
            """)
    Page<AccountType> findVisibleByCreatedUserId(
            @Param("createdUserId") Integer createdUserId,
            Pageable pageable);
}
