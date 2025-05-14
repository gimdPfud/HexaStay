package com.sixthsense.hexastay.service.impl;

import com.sixthsense.hexastay.entity.Room;
import com.sixthsense.hexastay.entity.Survey;
import com.sixthsense.hexastay.repository.HotelRoomRepository;
import com.sixthsense.hexastay.repository.MemberRepository;
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
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Properties;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final RoomRepository roomRepository;
    private final MemberRepository memberRepository;
    private final HotelRoomRepository hotelRoomRepository;
    
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

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "false");

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
        helper.setText(content, true);
        
        getHardcodedMailSender().send(message);
    }

    @Override
    public void sendSurveyEmail(String subject, String content, String memberEmail, String memberName, String roomName, Long roomNum, Room room) {
        try {
            Context context = new Context();

            Long companyNum = null;
            if (room.getHotelRoom() != null && room.getHotelRoom().getCompany() != null) {
                companyNum = room.getHotelRoom().getCompany().getCompanyNum();
            }
            
            Survey activeSurvey = null;
            if (companyNum != null) {
                activeSurvey = surveyService.getActiveSurveyByCompany(companyNum);
            } else {
                activeSurvey = surveyService.getActiveSurvey();
            }

            if (activeSurvey == null) {
                activeSurvey = new Survey();
                activeSurvey.setSurveyNum(1L);
                activeSurvey.setSurveyTitle(subject != null && !subject.isEmpty() ? subject : "고객님의 소중한 의견을 기다립니다.");
                activeSurvey.setSurveyContent(content != null && !content.isEmpty() ? content : 
                    "더 나은 서비스를 제공하기 위해 고객님의 의견을 듣고자 합니다.\n간단한 설문에 참여해 주시면 큰 도움이 됩니다. 소요 시간은 약 1분입니다.");
                activeSurvey.setSurveyIsActive(true);
            }

            String companyName = "헥사스테이";
            if (room.getHotelRoom() != null && 
                room.getHotelRoom().getCompany() != null &&
                room.getHotelRoom().getCompany().getCompanyName() != null) {
                companyName = room.getHotelRoom().getCompany().getCompanyName();
            }
            
            context.setVariable("survey", activeSurvey);
            context.setVariable("memberName", memberName);
            context.setVariable("memberEmail", memberEmail);
            context.setVariable("roomName", roomName != null ? roomName : "객실");
            context.setVariable("roomNum", roomNum);
            context.setVariable("companyName", companyName);
            context.setVariable("surveyTitle", activeSurvey.getSurveyTitle());
            context.setVariable("surveyContent", activeSurvey.getSurveyContent());
            context.setVariable("baseUrl", "http://wooriproject.iptime.org:9002");
            context.setVariable("room", room);

            String emailContent = templateEngine.process("survey/surveytemplate", context);

            MimeMessage message = getHardcodedMailSender().createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(FROM_EMAIL);
            helper.setTo(memberEmail);
            helper.setSubject(subject);
            helper.setText(emailContent, true);
            
            getHardcodedMailSender().send(message);
        } catch (Exception e) {
            throw new RuntimeException("이메일 발송 실패", e);
        }
    }
}