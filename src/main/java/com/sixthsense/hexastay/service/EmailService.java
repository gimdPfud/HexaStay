package com.sixthsense.hexastay.service;

import com.sixthsense.hexastay.entity.Room;
import jakarta.mail.MessagingException;

public interface EmailService {
    void sendEmail(String to, String subject, String content) throws MessagingException;
    void sendVerificationCode(String to, String verificationCode);
    void sendSurveyEmail(String subject, String content, String memberEmail, String memberName, String roomName, Long roomNum, Room room);
} 