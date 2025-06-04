package com.khata.mailVerification.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class MailVerificationDTO {

    @Email(message = "Invalid email address format.")
    @NotBlank(message = "Email cannot be blank.")
    @Size(max = 100, message = "Email must be less than 100 characters.")
    private String email;

    @NotBlank(message = "OTP cannot be blank")
    private String otp;
}
