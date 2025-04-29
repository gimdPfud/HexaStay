package com.sixthsense.hexastay.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    // Room 비밀번호 메일 발송용 메소드
    public void sendRoomPasswordEmail(String toEmail, String roomPassword) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(toEmail);                         // 받는 사람 이메일
        helper.setSubject("호텔 예약 비밀번호 안내");   // 메일 제목
        helper.setFrom("kimbbuhhwan@gmail.com");        // 보내는 사람

        // ✨ HTML 본문
        String htmlContent = "<div style='padding:20px; font-family:Arial;'>"
                + "<h2>호텔 예약 비밀번호 안내</h2>"
                + "<p>아래 비밀번호를 이용해 체크인 해주세요:</p>"
                + "<div style='font-size:30px; font-weight:bold; color:#4CAF50; margin:20px 0;'>"
                + roomPassword
                + "</div>"
                + "<p>좋은 하루 되세요!</p>"
                + "</div>";

        helper.setText(htmlContent, true); // HTML 적용
        mailSender.send(message);
    }
}
