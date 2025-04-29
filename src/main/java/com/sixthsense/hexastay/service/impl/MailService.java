package com.sixthsense.hexastay.service.impl;


import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.log4j.Log4j2;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

@Log4j2
@EnableAsync // ✨ 이 클래스 안에서만 @Async 활성화
@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;


    private String senderEmail = "kimbbuhhwan@gmail.com";


    // 메일 비동기 발송 (void 리턴으로 수정)
    @Async
    public void sendRoomPasswordEmail(String toEmail, String roomPassword) {
        log.info("📨 메일 발송 비동기 시작 - 수신자: {}", toEmail);

        try {
            // 메일 메시지 생성
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // 메일 기본 정보 설정
            helper.setTo(toEmail);
            helper.setSubject("호텔 예약 비밀번호 안내");
            helper.setFrom(senderEmail);

            // 메일 본문 내용 (HTML)
            String htmlContent = "<div style='padding:20px; font-family:Arial;'>"
                    + "<h2>호텔 예약 비밀번호 안내</h2>"
                    + "<p>아래 비밀번호를 이용해 체크인 해주세요:</p>"
                    + "<div style='font-size:30px; font-weight:bold; color:#4CAF50; margin:20px 0;'>"
                    + roomPassword
                    + "</div>"
                    + "<p>좋은 하루 되세요!</p>"
                    + "</div>";

            helper.setText(htmlContent, true); // HTML 본문 적용

            // 메일 전송
            mailSender.send(message);

            log.info("✅ 메일 발송 성공 - 수신자: {}", toEmail);

        } catch (Exception e) {
            log.error("❌ 메일 발송 실패 - 수신자: {}, 오류: {}", toEmail, e);
        }
    }
}
