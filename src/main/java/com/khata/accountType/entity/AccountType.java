package com.khata.accountType.entity;

import com.khata.accountType.entity.enums.AccountCategoryType;
import com.khata.accountType.entity.enums.TransactionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Entity
@Getter
@Setter
public class AccountType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 100, unique = true)
    private AccountCategoryType name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 100)
    private TransactionType transactionType;

    @Column(length = 300)
    private String description;
}
