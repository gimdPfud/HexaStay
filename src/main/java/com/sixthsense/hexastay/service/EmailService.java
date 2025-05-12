package com.sixthsense.hexastay.service;

import jakarta.mail.MessagingException;

public interface EmailService {
    void sendEmail(String to, String subject, String content) throws MessagingException;
    void sendVerificationCode(String to, String verificationCode);
    void sendSurveyEmail(String subject, String content, String memberName);
} 