package com.khata.settings.chartOfAccount.repositories;

import com.khata.settings.chartOfAccount.entity.ChartOfAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChartOfAccountRepo extends JpaRepository<ChartOfAccount, Integer> {
    @Query("""
            select count(chartOfAccount) > 0
            from ChartOfAccount chartOfAccount
            where chartOfAccount.name = :name
              and (chartOfAccount.createdUserId = :createdUserId or chartOfAccount.isSystemDefault = true)
            """)
    boolean existsVisibleByName(
            @Param("name") String name,
            @Param("createdUserId") Integer createdUserId);

    @Query("""
            select chartOfAccount
            from ChartOfAccount chartOfAccount
            where chartOfAccount.name = :name
              and (chartOfAccount.createdUserId = :createdUserId or chartOfAccount.isSystemDefault = true)
            """)
    Optional<ChartOfAccount> findVisibleByName(
            @Param("name") String name,
            @Param("createdUserId") Integer createdUserId);

    Optional<ChartOfAccount> findByNameAndCreatedUserId(String name, Integer createdUserId);

    @Query("""
            select chartOfAccount
            from ChartOfAccount chartOfAccount
            where chartOfAccount.id = :id
              and (chartOfAccount.createdUserId = :createdUserId or chartOfAccount.isSystemDefault = true)
            """)
    Optional<ChartOfAccount> findVisibleById(
            @Param("id") Integer id,
            @Param("createdUserId") Integer createdUserId);

    @Query("""
            select chartOfAccount
            from ChartOfAccount chartOfAccount
            where chartOfAccount.createdUserId = :createdUserId or chartOfAccount.isSystemDefault = true
            """)
    Page<ChartOfAccount> findVisibleByCreatedUserId(
            @Param("createdUserId") Integer createdUserId,
            Pageable pageable);
}
