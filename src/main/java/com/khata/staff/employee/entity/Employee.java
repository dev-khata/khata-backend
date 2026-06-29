package com.khata.staff.employee.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(
        name = "employee",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"created_user_id", "employee_code"}),
                @UniqueConstraint(columnNames = {"created_user_id", "phone_number"})
        }
)
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, name = "employee_code", length = 50)
    private String employeeCode;

    @Column(nullable = false, name = "full_name", length = 100)
    private String fullName;

    @Column(nullable = false, name = "phone_number", length = 10)
    private String phoneNumber;

    @Column(nullable = false, length = 100)
    private String address;

    @Column(nullable = false, name = "joining_date_in_nepali", length = 10)
    private String joiningDateInNepali;

    @Column(nullable = false, name = "joining_date_in_english")
    private LocalDate joiningDateInEnglish;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false, name = "created_user_id")
    private Integer createdUserId;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EmployeeDepartment> departments = new ArrayList<>();
}
