package com.sixthsense.hexastay.service.impl;

import com.sixthsense.hexastay.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
@RequiredArgsConstructor
@Log4j2
public class EmailServiceImpl implements EmailService {

    private static final String FROM_EMAIL = "welstorypark@gmail.com";
    private static final String EMAIL_PASSWORD = "xetmyozkhkbwwwej"; // Gmail 앱 비밀번호

    private JavaMailSender getHardcodedMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername(FROM_EMAIL);
        mailSender.setPassword(EMAIL_PASSWORD); // 앱 비밀번호 설정

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }

    @Override
    @Async
    public void sendVerificationCode(String to, String verificationCode) {
        try {
            JavaMailSender mailSender = getHardcodedMailSender(); // 하드코딩된 메일 설정 사용
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(FROM_EMAIL); // 보내는 이메일 주소
            helper.setTo(to); // 수신자 이메일 주소
            helper.setSubject("HexaStay 비밀번호 찾기 인증번호");

            String htmlContent = """
    <div style="font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; padding: 20px; border: 1px solid #ddd; border-radius: 8px; max-width: 500px; margin: auto;">
        <h2 style="color: #4CAF50;">HexaStay 인증번호 안내</h2>
        <p style="font-size: 16px; color: #333;">
            안녕하세요, HexaStay 비밀번호 찾기 요청에 따라 아래 인증번호를 보내드립니다.
        </p>
        <p style="font-size: 18px; font-weight: bold; background-color: #f7f7f7; padding: 15px; border-radius: 6px; text-align: center; letter-spacing: 2px; color: #222;">
            인증번호: <span style="color: #4CAF50;">%s</span>
        </p>
        <p style="font-size: 14px; color: #666;">
            요청하지 않으셨다면 이 이메일을 무시해 주세요.
        </p>
        <hr style="margin-top: 30px;"/>
        <p style="font-size: 12px; color: #aaa;">
            이 메일은 발신 전용입니다. HexaStay 고객센터에 문의가 필요하시면 홈페이지를 방문해 주세요.
        </p>
    </div>
""".formatted(verificationCode);

            helper.setText(htmlContent, true); // HTML 형식으로 메일 내용 설정

            mailSender.send(message); // 이메일 전송
            log.info("인증번호 이메일 전송 완료: {}", to);
        } catch (MessagingException e) {
            log.error("인증번호 이메일 전송 실패: ", e);
            throw new RuntimeException("이메일 전송에 실패했습니다.", e);
        }
    }
}