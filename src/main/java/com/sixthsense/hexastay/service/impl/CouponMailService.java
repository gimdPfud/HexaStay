package com.sixthsense.hexastay.service.impl;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Log4j2
@EnableAsync // ✨ 이 클래스 안에서만 @Async 활성화
@Service
@RequiredArgsConstructor

public class CouponMailService {

    private final JavaMailSender mailSender;


    private String senderEmail = "kimbbuhhwan@gmail.com";

    @Async
    public void sendCouponEmail(String toEmail, String couponCode, int discountRate, LocalDate expirationDate) {
        log.info("📨 쿠폰 메일 발송 시작 - 수신자: {}", toEmail);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("🎁 할인 쿠폰이 도착했습니다!");
            helper.setFrom(senderEmail);

            String htmlContent = "<div style='padding:20px; font-family:Arial;'>"
                    + "<h2>🎉 축하합니다! 할인 쿠폰이 발급되었습니다</h2>"
                    + "<p>아래 쿠폰 정보를 확인하고 사용하세요.</p>"
                    + "<div style='font-size:24px; font-weight:bold; color:#ff5722;'>쿠폰 코드: " + couponCode + "</div>"
                    + "<div style='font-size:18px;'>할인율: " + discountRate + "%</div>"
                    + "<div style='font-size:18px;'>유효기간: " + expirationDate + "</div>"
                    + "<p style='margin-top:20px;'>마이페이지에서 쿠폰을 확인하고 사용하세요.</p>"
                    + "</div>";

            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("✅ 쿠폰 메일 발송 성공 - 수신자: {}", toEmail);
        } catch (Exception e) {
            log.error("❌ 쿠폰 메일 발송 실패 - 수신자: {}, 오류: {}", toEmail, e);
        }
    }
}
