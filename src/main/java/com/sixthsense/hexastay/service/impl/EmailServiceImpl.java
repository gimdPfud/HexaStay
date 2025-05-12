package com.sixthsense.hexastay.service.impl;

import com.sixthsense.hexastay.entity.Survey;
import com.sixthsense.hexastay.entity.Member;
import com.sixthsense.hexastay.entity.Room;
import com.sixthsense.hexastay.repository.RoomRepository;
import com.sixthsense.hexastay.repository.MemberRepository;
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
    private final MemberRepository memberRepository;
    
    @Value("${spring.mail.username}")
    private String FROM_EMAIL;
    
    @Value("${spring.mail.password}")
    private String EMAIL_PASSWORD;

    private final TemplateEngine templateEngine;

    private final SurveyService surveyService;


    private JavaMailSender getHardcodedMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername("welstorypark@gmail.com");
        mailSender.setPassword("xkurhrjcqgyabgbk");

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
    public void sendSurveyEmail(String subject, String content, String memberEmail) {
        try {
            Context context = new Context();
            Survey activeSurvey = surveyService.getActiveSurvey();
            if (activeSurvey == null) {
                log.error("활성화된 설문조사가 없습니다.");
                return;
            }
            
            // 회원 정보 조회
            Member member = memberRepository.findByMemberEmail(memberEmail);
            if (member == null) {
                log.error("회원 정보를 찾을 수 없습니다: {}", memberEmail);
                return;
            }
            
            // 최근 체크아웃한 방 정보 조회
            Room room = roomRepository.findTopByMemberOrderByCheckOutDateDesc(member)
                    .orElseThrow(() -> new RuntimeException("방 정보를 찾을 수 없습니다."));
            
            context.setVariable("survey", activeSurvey);
            context.setVariable("memberName", member.getMemberName());
            context.setVariable("memberEmail", memberEmail);
            context.setVariable("roomName", room.getHotelRoom().getHotelRoomName());
            context.setVariable("roomNum", room.getRoomNum());
            context.setVariable("companyName", room.getHotelRoom().getCompany().getCompanyName());
            context.setVariable("surveyTitle", activeSurvey.getSurveyTitle());
            context.setVariable("surveyContent", activeSurvey.getSurveyContent());
            context.setVariable("baseUrl", "http://localhost:8090");

            String emailContent = templateEngine.process("survey/surveytemplate", context);

            MimeMessage message = getHardcodedMailSender().createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(FROM_EMAIL);
            helper.setTo(memberEmail);
            helper.setSubject(subject);
            helper.setText(emailContent, true);
            
            getHardcodedMailSender().send(message);
            log.info("설문조사 이메일 전송 완료: {}", memberEmail);
        } catch (Exception e) {
            log.error("설문조사 이메일 발송 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("이메일 발송 실패", e);
        }
    }
}