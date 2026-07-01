package com.khata.settings.accountType.entity;

import com.khata.settings.accountType.entity.enums.TransactionType;
import com.khata.settings.chartOfAccount.entity.ChartOfAccount;
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
        name = "account_type",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_account_type_user_name", columnNames = {"created_user_id", "name"})
        }
)
public class AccountType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 100)
    private TransactionType transactionType;

    private boolean isSystemDefault = false;

    @Column(length = 300)
    private String description;

    @Column(name = "created_user_id")
    private Integer createdUserId;

    @OneToMany(mappedBy = "accountType", cascade = CascadeType.ALL)
    private List<ChartOfAccount> chatOfAccounts = new ArrayList<>();
}
