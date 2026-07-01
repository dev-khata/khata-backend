package com.khata.staff.employee.dto;

import com.khata.staff.employee.entity.enums.EmployeePaymentType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor
@Getter
@Setter
public class EmployeeDepartmentAssignmentDTO {

    @NotNull(message = "Department cannot be null.")
    private Integer departmentId;

    private String departmentName;

    @NotNull(message = "Payment type cannot be null.")
    private EmployeePaymentType paymentType;

    @DecimalMin(value = "0.0", message = "Monthly salary must be greater than or equal to 0.")
    private BigDecimal monthlySalary;
}
