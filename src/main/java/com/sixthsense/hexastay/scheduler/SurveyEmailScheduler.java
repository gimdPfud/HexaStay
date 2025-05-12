package com.sixthsense.hexastay.scheduler;

import com.sixthsense.hexastay.entity.Survey;
import com.sixthsense.hexastay.service.EmailService;
import com.sixthsense.hexastay.service.SurveyService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sixthsense.hexastay.entity.Room;
import com.sixthsense.hexastay.repository.RoomRepository;

@Component
@RequiredArgsConstructor
public class SurveyEmailScheduler {
    private final SurveyService surveyService;
    private final EmailService emailService;
    private final RoomRepository roomRepository;
    private static final Logger log = LoggerFactory.getLogger(SurveyEmailScheduler.class);

    @Scheduled(cron = "0 0 10 * * ?") // 매일 오전 10시에 실행
    public void sendSurveyEmails() {
        try {
            Survey activeSurvey = surveyService.getActiveSurvey();
            if (activeSurvey != null) {
                String title = activeSurvey.getSurveyTitle();
                String content = activeSurvey.getSurveyContent();
                
                // 체크아웃한 고객들의 이메일 주소 목록을 가져옴
                List<Room> checkedOutRooms = roomRepository.findByCheckOutDateBetween(
                    LocalDateTime.now().minusDays(1).withHour(0).withMinute(0).withSecond(0),
                    LocalDateTime.now().minusDays(1).withHour(23).withMinute(59).withSecond(59)
                );

                for (Room room : checkedOutRooms) {
                    if (room.getMember() != null && room.getMember().getMemberEmail() != null) {
                        String memberName = room.getMember().getMemberName();
                        emailService.sendSurveyEmail(title, content, memberName);
                    }
                }
            }
        } catch (Exception e) {
            log.error("설문조사 이메일 발송 중 오류 발생: {}", e.getMessage());
        }
    }
} 