package com.khata.party.repositories;

import com.khata.party.entity.PartyRecord;
import com.khata.party.entity.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface PartyRecordRepo extends JpaRepository<PartyRecord, Integer> {
    Page<PartyRecord> findByPartyIdAndParticularContainingIgnoreCase(Integer partyId, String particular, Pageable pageable);

    @Query("""
            select partyRecord
            from PartyRecord partyRecord
            where partyRecord.party.id = :partyId
              and partyRecord.party.createdUserID = :createdUserId
              and partyRecord.fiscalYear.id = :fiscalYearId
            """)
    Page<PartyRecord> findByPartyIdAndCreatedUserIdAndFiscalYearId(
            @Param("partyId") Integer partyId,
            @Param("createdUserId") Integer createdUserId,
            @Param("fiscalYearId") Integer fiscalYearId,
            Pageable pageable);

    @Query("""
            select coalesce(sum(
                case
                    when partyRecord.transactionType = com.khata.party.entity.enums.TransactionType.DEBIT
                        then partyRecord.amount
                    else -partyRecord.amount
                end
            ), 0)
            from PartyRecord partyRecord
            where partyRecord.party.id = :partyId
              and partyRecord.party.createdUserID = :createdUserId
            """)
    BigDecimal calculateNetBalanceByPartyIdAndCreatedUserId(
            @Param("partyId") Integer partyId,
            @Param("createdUserId") Integer createdUserId);

    @Query("""
            select coalesce(sum(partyRecord.amount), 0)
            from PartyRecord partyRecord
            where partyRecord.party.id = :partyId
              and partyRecord.party.createdUserID = :createdUserId
              and partyRecord.fiscalYear.id = :fiscalYearId
              and partyRecord.transactionType = :transactionType
            """)
    BigDecimal calculateTotalByPartyIdAndCreatedUserIdAndFiscalYearIdAndTransactionType(
            @Param("partyId") Integer partyId,
            @Param("createdUserId") Integer createdUserId,
            @Param("fiscalYearId") Integer fiscalYearId,
            @Param("transactionType") TransactionType transactionType);
}
