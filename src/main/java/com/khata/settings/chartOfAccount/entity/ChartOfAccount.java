package com.khata.settings.chartOfAccount.entity;

import com.khata.settings.accountType.entity.AccountType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Entity
@Getter
@Setter
public class ChartOfAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;

    @Column(nullable = false, length = 100, unique = true)
    private String name;

    @Column(length = 300)
    private String description;

    @ManyToOne
    @JoinColumn(name = "account_type_id")
    private AccountType accountType;

    private boolean isActive = true;

    private  boolean isSystemDefault = false;
}
