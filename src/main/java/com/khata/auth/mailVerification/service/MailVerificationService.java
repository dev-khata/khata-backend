package com.khata.auth.mailVerification.service;

public interface MailVerificationService {
    void sendVerificationEmail(String email);

    void verifyOTP(String email, String otp);
}
