package com.khata.auth.mailVerification.controller;

import com.khata.auth.payload.JwtAuthResponse;
import com.khata.auth.service.AuthService;
import com.khata.exceptions.ApiException;
import com.khata.auth.mailVerification.dto.MailVerificationDTO;
import com.khata.auth.mailVerification.service.MailVerificationService;
import com.khata.payload.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/email")
@Tag(name = "Mail Verification")
public class MailVerificationController {

    private final MailVerificationService mailVerificationService;
    private final AuthService authService;

    public MailVerificationController(MailVerificationService mailVerificationService, AuthService authService) {
        this.mailVerificationService = mailVerificationService;
        this.authService = authService;
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<?>> verifyEmail(@RequestBody MailVerificationDTO verificationDTO) {
        try {
            String username = verificationDTO.getEmail();
            mailVerificationService.verifyOTP(username, verificationDTO.getOtp());

            JwtAuthResponse jwtAuthResponse = authService.autoLoginAfterVerification(verificationDTO.getEmail());

            return ResponseEntity.ok(new ApiResponse<>(jwtAuthResponse, HttpStatus.OK.value(), "Email verified successfully"));
        } catch (ApiException | IllegalArgumentException ex) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(null, HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), "Something went wrong"));
        }
    }

    @PostMapping("/resend")
    public ResponseEntity<ApiResponse<Void>> resendVerificationEmail(@RequestBody MailVerificationDTO verificationDTO) {
        try {
            mailVerificationService.sendVerificationEmail(verificationDTO.getEmail());
            return ResponseEntity.ok(new ApiResponse<>(null, HttpStatus.OK.value(), "Verification email sent successfully"));
        } catch (ApiException ex) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(null, HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to send verification email"));
        }
    }


}
