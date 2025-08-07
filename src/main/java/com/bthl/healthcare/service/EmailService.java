package com.bthl.healthcare.service;

import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.MailException;
import org.springframework.scheduling.annotation.Async;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * File: /Users/dstjohn/dev/02_davestj.com/bthl-hc/src/main/java/com/bthl/healthcare/service/EmailService.java
 * Author: davestj (David St John)
 * Date: 2025-07-18
 * Purpose: Email service for BTHL-HealthCare platform notifications and communications
 * Git Commit: git commit -m "feat: add EmailService basic implementation for compilation resolution"
 * Next Dev Feature: Add complete email templating and SMTP configuration
 */

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    private void sendEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
            logger.debug("Sent email to {} with subject {}", to, subject);
        } catch (MailException e) {
            logger.error("Failed to send email to {}", to, e);
        }
    }
    
    public void sendUserRegistrationEmail(String email, String firstName, String lastName, String username, String token) {
        logger.info("I am sending registration email to: {}", email);
        String subject = "Welcome to BTHL-HealthCare";
        String body = String.format("Hello %s %s,\n\nYour account %s has been registered. Verification token: %s", firstName, lastName, username, token);
        sendEmail(email, subject, body);
    }
    
    public void sendPasswordResetEmail(String email, String firstName, String token) {
        logger.info("I am sending password reset email to: {}", email);
        String subject = "Password Reset";
        String body = String.format("Hello %s,\n\nUse the following token to reset your password: %s", firstName, token);
        sendEmail(email, subject, body);
    }
    
    public void sendMfaVerificationEmail(String email, String firstName, String code) {
        logger.info("I am sending MFA verification email to: {}", email);
        String subject = "MFA Verification";
        String body = String.format("Hello %s,\n\nYour MFA verification code is: %s", firstName, code);
        sendEmail(email, subject, body);
    }
    
    public void sendSimpleEmail(String email, String subject, String text) {
        logger.info("I am sending simple email to: {}", email);
        sendEmail(email, subject, text);
    }

    @Async
    public void sendEmailVerificationAsync(String email, String token) {
        logger.info("Sending email verification to: {}", email);
        String subject = "Verify Your Email";
        String body = String.format("Please verify your email using this token: %s", token);
        sendEmail(email, subject, body);
    }

    @Async
    public void sendAccountLockoutNotificationAsync(String email, int lockoutMinutes) {
        logger.info("Sending account lockout notification to: {}", email);
        String subject = "Account Locked";
        String body = String.format("Your account has been locked for %d minutes due to multiple failed login attempts.", lockoutMinutes);
        sendEmail(email, subject, body);
    }

    @Async
    public void sendPasswordChangeNotificationAsync(String email) {
        logger.info("Sending password change notification to: {}", email);
        String subject = "Password Changed";
        String body = "Your password has been changed. If you did not initiate this change, please contact support immediately.";
        sendEmail(email, subject, body);
    }

    @Async
    public void sendPasswordResetEmailAsync(String email, String resetToken) {
        logger.info("Sending password reset email to: {}", email);
        String subject = "Password Reset";
        String body = String.format("Use the following token to reset your password: %s", resetToken);
        sendEmail(email, subject, body);
    }

    @Async
    public void sendPasswordResetConfirmationAsync(String email) {
        logger.info("Sending password reset confirmation to: {}", email);
        String subject = "Password Reset Confirmation";
        String body = "Your password has been successfully reset.";
        sendEmail(email, subject, body);
    }
}
