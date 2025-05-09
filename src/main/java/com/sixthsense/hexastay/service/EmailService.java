package com.sixthsense.hexastay.service;

public interface EmailService {
    void sendVerificationCode(String to, String verificationCode);
} 