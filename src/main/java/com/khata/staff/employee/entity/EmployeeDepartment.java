package com.khata.staff.employee.entity;

import com.khata.settings.department.entity.Department;
import com.khata.staff.employee.entity.enums.EmployeePaymentType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(
        name = "employee_department",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"employee_id", "department_id"})
        }
)
public class EmployeeDepartment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "payment_type", length = 20)
    private EmployeePaymentType paymentType;

    @Column(name = "monthly_salary", precision = 19, scale = 2)
    private BigDecimal monthlySalary;
}
