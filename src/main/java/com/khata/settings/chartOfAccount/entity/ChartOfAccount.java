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
@Table(
        name = "chart_of_account",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_chart_of_account_user_name", columnNames = {"created_user_id", "name"})
        }
)
public class ChartOfAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 300)
    private String description;

    @ManyToOne
    @JoinColumn(name = "account_type_id")
    private AccountType accountType;

    private boolean isActive = true;

    private boolean isSystemDefault = false;

    @Column(name = "created_user_id")
    private Integer createdUserId;
}
