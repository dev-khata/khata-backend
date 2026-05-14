package com.khata.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.khata.auth.entity.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class UserDTO {
    @JsonIgnore
    private Integer id;

    @NotBlank(message = "Full name cannot be blank.")
    @Size(min = 4, max = 100, message = "Full name must be between 4 and 100 characters.")
    private String fullName;

    @Email(message = "Invalid email address format.")
    @NotBlank(message = "Email cannot be blank.")
    @Size(max = 100, message = "Email must be less than 100 characters.")
    private String email;

    @NotBlank(message = "Password cannot be blank.")
    @Size(min = 7, max = 100, message = "Password must be between 7 and 100 characters.")
    private String password;

    @Size(max = 15, message = "Phone number must be less than or equal to 15 digits.")
    private String phoneNumber;

    private Role role = Role.USER;

    private boolean isVerified;

    @JsonIgnore
    public String getPassword() {
        return this.password;
    }

    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
    }
}
