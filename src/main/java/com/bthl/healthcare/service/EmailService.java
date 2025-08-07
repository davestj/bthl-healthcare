package com.bthl.healthcare.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

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

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendEmailVerificationAsync(String email, String token) {
        String subject = "Verify Your Email";
        String text = "Please verify your email using this token: " + token;
        sendMessage(email, subject, text);
    }

    @Async
    public void sendAccountLockoutNotificationAsync(String email, int lockoutMinutes) {
        String subject = "Account Locked";
        String text = "Your account has been locked for " + lockoutMinutes
                + " minutes due to multiple failed login attempts.";
        sendMessage(email, subject, text);
    }

    @Async
    public void sendPasswordChangeNotificationAsync(String email) {
        String subject = "Password Changed";
        String text = "Your password was recently changed. If this wasn't you, please contact support immediately.";
        sendMessage(email, subject, text);
    }

    @Async
    public void sendPasswordResetEmailAsync(String email, String resetToken) {
        String subject = "Password Reset Request";
        String text = "Use the following token to reset your password: " + resetToken;
        sendMessage(email, subject, text);
    }

    @Async
    public void sendPasswordResetConfirmationAsync(String email) {
        String subject = "Password Reset Confirmation";
        String text = "Your password has been reset successfully.";
        sendMessage(email, subject, text);
    }

    public void sendSimpleEmail(String email, String subject, String text) {
        sendMessage(email, subject, text);
    }

    private void sendMessage(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
            logger.debug("Sent email to {}", to);
        } catch (Exception e) {
            logger.error("Failed to send email to {}", to, e);
        }
    }

    public void sendUserRegistrationEmail(String email, String firstName, String lastName, String username, String token) {
        logger.info("I am sending registration email to: {}", email);
        // TODO: Implement email sending functionality
    }

    public void sendPasswordResetEmail(String email, String firstName, String token) {
        logger.info("I am sending password reset email to: {}", email);
        // TODO: Implement password reset email
    }

    public void sendMfaVerificationEmail(String email, String firstName, String code) {
        logger.info("I am sending MFA verification email to: {}", email);
        // TODO: Implement MFA email
    }
}
