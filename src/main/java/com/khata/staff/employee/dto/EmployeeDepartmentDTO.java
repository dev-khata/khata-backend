package com.khata.staff.employee.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class EmployeeDepartmentDTO {

    @NotNull(message = "Department cannot be null.")
    private Integer departmentId;

    private String departmentName;
}
