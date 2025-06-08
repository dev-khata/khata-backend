package com.khata.accountType.entity;

import com.khata.accountType.entity.enums.TransactionType;
import com.khata.chartOfAccount.entity.ChartOfAccount;
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
public class AccountType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 100)
    private TransactionType transactionType;

    private boolean isSystemDefined = false;

    @Column(length = 300)
    private String description;

    @OneToMany(mappedBy = "accountType", cascade = CascadeType.ALL)
    private List<ChartOfAccount> chatOfAccounts = new ArrayList<>();
}
