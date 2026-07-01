package com.khata.staff.employee.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class EmployeeDTO {

    private Integer id;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Size(max = 50, message = "Employee code must be less than 50 characters.")
    private String employeeCode;

    @NotBlank(message = "Full name cannot be blank.")
    @Size(max = 100, message = "Full name must be less than 100 characters.")
    private String fullName;

    @NotBlank(message = "Phone number cannot be blank.")
    @Pattern(regexp = "^(97|98)[0-9]{8}$", message = "Phone number must be 10 digits and start with 97 or 98.")
    private String phoneNumber;

    @NotBlank(message = "Address cannot be blank.")
    @Size(max = 100, message = "Address must be less than 100 characters.")
    private String address;

    @NotBlank(message = "Joining Nepali date cannot be blank.")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Joining Nepali date must be in yyyy-MM-dd format.")
    private String joiningDateInNepali;

    @NotNull(message = "Joining English date cannot be null.")
    private LocalDate joiningDateInEnglish;

    private Boolean active = true;

    @NotEmpty(message = "At least one department assignment is required.")
    private List<@Valid EmployeeDepartmentAssignmentDTO> departmentAssignments = new ArrayList<>();
}
