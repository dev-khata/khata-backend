package com.khata.party.entity;

import com.khata.party.entity.enums.PartyType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(
        name = "party",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"created_user_id", "email"}),
                @UniqueConstraint(columnNames = {"created_user_id", "phone_number"})
        }
)
public class Party {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(length = 10)
    private String phoneNumber;

    @Column(nullable = false, length = 100)
    private String address;

    @Column(nullable = false, length = 100)
    private String partyBusinessName;

    @Column(length = 100)
    private String cbf;

//    @Column(nullable = false, precision = 19, scale = 2)
//    private BigDecimal openingBalance;
//
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false, length = 100)
//    private TransactionType transactionType;

    @Column(nullable = false, name = "created_user_id")
    private Integer createdUserID;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private PartyType partyType;

    @OneToMany(mappedBy = "party", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PartyRecord> partyRecordList = new ArrayList<>();
}
