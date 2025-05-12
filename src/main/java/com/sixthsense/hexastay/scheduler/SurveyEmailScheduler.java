package com.sixthsense.hexastay.scheduler;

import com.sixthsense.hexastay.entity.Survey;
import com.sixthsense.hexastay.service.EmailService;
import com.sixthsense.hexastay.service.SurveyService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sixthsense.hexastay.entity.Room;
import com.sixthsense.hexastay.entity.Member;
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

                // 멤버별로 중복 제거 (한 사람이 여러 방을 체크아웃한 경우 한 번만 이메일 발송)
                Map<String, Room> memberEmailToRoom = new HashMap<>();
                for (Room room : checkedOutRooms) {
                    if (room.getMember() != null && room.getMember().getMemberEmail() != null) {
                        String memberEmail = room.getMember().getMemberEmail();
                        // 이미 처리한 이메일이 아닌 경우에만 맵에 추가
                        if (!memberEmailToRoom.containsKey(memberEmail)) {
                            memberEmailToRoom.put(memberEmail, room);
                        }
                    }
                }
                
                // 중복 제거된 맵을 순회하며 이메일 발송
                for (Map.Entry<String, Room> entry : memberEmailToRoom.entrySet()) {
                    String memberEmail = entry.getKey();
                    Room room = entry.getValue();
                    String memberName = room.getMember().getMemberName();
                    
                    try {
                        // 직접 이메일 정보 설정
                        emailService.sendSurveyEmail(title, content, memberEmail);
                        log.info("설문조사 이메일 전송 완료: {} ({})", memberName, memberEmail);
                    } catch (Exception e) {
                        log.error("설문조사 이메일 발송 중 오류 발생 ({}): {}", memberEmail, e.getMessage());
                    }
                }
            } else {
                log.warn("활성화된 설문조사가 없습니다.");
            }
        } catch (Exception e) {
            log.error("설문조사 이메일 발송 중 오류 발생: {}", e.getMessage());
        }
    }
} 