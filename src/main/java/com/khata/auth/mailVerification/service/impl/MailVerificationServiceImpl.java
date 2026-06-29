package com.khata.auth.mailVerification.service.impl;

import com.khata.auth.entity.User;
import com.khata.auth.repositories.UserRepo;
import com.khata.exceptions.ApiException;
import com.khata.auth.mailVerification.entity.MailVerification;
import com.khata.auth.mailVerification.repositories.MailVerificationRepo;
import com.khata.auth.mailVerification.service.MailVerificationService;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class MailVerificationServiceImpl implements MailVerificationService {

    private final JavaMailSender mailSender;
    private final MailVerificationRepo mailVerificationRepo;
    private final UserRepo userRepo;

    public MailVerificationServiceImpl(
            JavaMailSender mailSender,
            MailVerificationRepo mailVerificationRepo,
            UserRepo userRepo) {
        this.mailSender = mailSender;
        this.mailVerificationRepo = mailVerificationRepo;
        this.userRepo = userRepo;
    }

    @Override
    @Transactional
    public void sendVerificationEmail(String emailId) {
        validateUser(emailId);
        MailVerification verification = prepareVerificationEntry(emailId);
        mailVerificationRepo.save(verification);
        sendOtpEmail(emailId, verification.getOtp());
    }

    @Override
    @Transactional
    public void verifyOTP(String email, String otp) {
        MailVerification verification = mailVerificationRepo.findByEmail(email)
                .orElseThrow(() -> new ApiException("No verification request found for the provided email."));

        if (!verification.getOtp().equals(otp)) {
            throw new IllegalArgumentException("Invalid OTP provided.");
        }

        if (isOtpExpired(verification)) {
            throw new IllegalArgumentException("The OTP has expired. Please request a new one.");
        }

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found."));

        user.setVerified(true);
        userRepo.save(user);
        mailVerificationRepo.delete(verification);

        log.info("{} has been successfully verified", email);
    }

    private void validateUser(String emailId) {
        User user = userRepo.findByEmail(emailId)
                .orElseThrow(() -> new ApiException("No user found with this email. Please register first."));

        if (user.isVerified()) {
            throw new ApiException("This email is already verified.");
        }
    }

    private MailVerification prepareVerificationEntry(String emailId) {
        String otp = generateOTP();
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(10);

        return mailVerificationRepo.findByEmail(emailId)
                .map(existing -> {
                    existing.setOtp(otp);
                    existing.setExpiryTime(expiry);
                    existing.setUsed(false);
                    return existing;
                })
                .orElseGet(() -> {
                    MailVerification newVerification = new MailVerification();
                    newVerification.setEmail(emailId);
                    newVerification.setOtp(otp);
                    newVerification.setExpiryTime(expiry);
                    newVerification.setUsed(false);
                    return newVerification;
                });
    }

    private void sendOtpEmail(String emailId, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(emailId);
            helper.setSubject("Your Khata Verification Code");

            String htmlMsg = "<p>Dear User,</p>"
                    + "<p>Thank you for using <strong>Khata</strong>.</p>"
                    + "<p>Your 6-digit verification code is: <strong>" + otp + "</strong></p>"
                    + "<p>Please enter this code in the app to complete your verification.</p>"
                    + "<p style='color:red;'><strong>Note:</strong> This code will expire in 10 minutes.</p>"
                    + "<br><p>Best regards,<br>The <strong>Khata</strong> Team</p>";

            helper.setText(htmlMsg, true);
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send verification email");
        }
    }

    private String generateOTP() {
        return String.valueOf((int) (Math.random() * 900000) + 100000);
    }

    private boolean isOtpExpired(MailVerification verification) {
        return verification.getExpiryTime().isBefore(LocalDateTime.now());
    }

}
