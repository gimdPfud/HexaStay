package com.sixthsense.hexastay.service.impl;

import com.sixthsense.hexastay.repository.RoomRepository;
import com.sixthsense.hexastay.service.EmailService;
import com.sixthsense.hexastay.service.SurveyService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final RoomRepository roomRepository;
    
    @Value("${spring.mail.username}")
    private String FROM_EMAIL;
    
    @Value("${spring.mail.password}")
    private String EMAIL_PASSWORD;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private SurveyService surveyService;

    @Autowired
    private JavaMailSender javaMailSender;

    private JavaMailSender getHardcodedMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername(FROM_EMAIL);
        mailSender.setPassword(EMAIL_PASSWORD); // 앱 비밀번호 설정

        java.util.Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }

    @Override
    @Async
    public void sendVerificationCode(String to, String verificationCode) {
        String subject = "헥사스테이 이메일 인증 코드";
        String content = String.format("""
            <html>
            <body>
                <h2>이메일 인증 코드</h2>
                <p>아래의 인증 코드를 입력해주세요:</p>
                <h3 style="color: #1D6F42;">%s</h3>
                <p>이 인증 코드는 5분간 유효합니다.</p>
            </body>
            </html>
            """, verificationCode);
        
        try {
            sendEmail(to, subject, content);
        } catch (MessagingException e) {
            throw new RuntimeException("이메일 발송 실패", e);
        }
    }

    @Override
    public void sendEmail(String to, String subject, String content) throws MessagingException {
        MimeMessage message = getHardcodedMailSender().createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, true); // HTML 형식으로 설정
        
        getHardcodedMailSender().send(message);
    }

    @Override
    public void sendSurveyEmail(String subject, String content, String memberName) {
        try {
            // 어제 체크아웃한 고객들의 이메일 주소 조회
            LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
            LocalDateTime startOfYesterday = yesterday.toLocalDate().atStartOfDay();
            LocalDateTime endOfYesterday = yesterday.toLocalDate().atTime(23, 59, 59);
            
            List<String> recipientEmails = roomRepository.findByCheckOutDateBetween(startOfYesterday, endOfYesterday)
                .stream()
                .map(room -> room.getMember().getMemberEmail())
                .collect(Collectors.toList());

            // Thymeleaf 컨텍스트 생성
            Context context = new Context();
            context.setVariable("survey", surveyService.getActiveSurvey());
            context.setVariable("memberName", memberName);

            // 템플릿 처리
            String emailContent = templateEngine.process("survey/survey-template", context);

            // 이메일 전송
            for (String email : recipientEmails) {
                try {
                    MimeMessage message = javaMailSender.createMimeMessage();
                    MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                    
                    helper.setFrom(FROM_EMAIL);
                    helper.setTo(email);
                    helper.setSubject(subject);
                    helper.setText(emailContent, true); // HTML 형식으로 설정
                    
                    javaMailSender.send(message);
                    log.info("설문조사 이메일 전송 성공: {}", email);
                } catch (Exception e) {
                    log.error("설문조사 이메일 전송 실패: {} - {}", email, e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("설문조사 이메일 전송 중 오류 발생: {}", e.getMessage());
        }
    }
}