package com.sixthsense.hexastay.scheduler;

import com.sixthsense.hexastay.entity.Room;
import com.sixthsense.hexastay.entity.Survey;
import com.sixthsense.hexastay.repository.RoomRepository;
import com.sixthsense.hexastay.service.EmailService;
import com.sixthsense.hexastay.service.SurveyService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SurveyEmailScheduler {
    private final SurveyService surveyService;
    private final EmailService emailService;
    private final RoomRepository roomRepository;
    private static final Logger log = LoggerFactory.getLogger(SurveyEmailScheduler.class);
    
    // 기본 설문조사 템플릿
    private static final String DEFAULT_SURVEY_TITLE = "고객님의 소중한 의견을 기다립니다.";
    private static final String DEFAULT_SURVEY_CONTENT = "더 나은 서비스를 제공하기 위해 고객님의 의견을 듣고자 합니다.\n간단한 설문에 참여해 주시면 큰 도움이 됩니다. 소요 시간은 약 1분입니다.";

    @Scheduled(cron = "0 0 10 * * ?") // 매일 오전 10시에 실행
    public void sendSurveyEmails() {
        try {
            // 체크아웃한 고객들의 방 목록을 가져옴 (전날 체크아웃한 방만)
            List<Room> checkedOutRooms = roomRepository.findByCheckOutDateBetween(
                LocalDateTime.now().minusDays(1).withHour(0).withMinute(0).withSecond(0),
                LocalDateTime.now().minusDays(1).withHour(23).withMinute(59).withSecond(59)
            );

            log.info("체크아웃 방 {}개에 대해 설문조사 이메일 발송 시작", checkedOutRooms.size());
            
            // 각 방에 대해 이메일 발송
            for (Room room : checkedOutRooms) {
                if (room.getMember() != null && room.getMember().getMemberEmail() != null &&
                    room.getHotelRoom() != null && room.getHotelRoom().getCompany() != null) {
                    
                    // 객실이 속한 회사 정보 가져오기
                    Long companyNum = room.getHotelRoom().getCompany().getCompanyNum();
                    
                    // 해당 회사의 활성화된 설문조사 찾기
                    Survey activeSurvey = surveyService.getActiveSurveyByCompany(companyNum);
                    
                    String memberEmail = room.getMember().getMemberEmail();
                    String memberName = room.getMember().getMemberName();
                    String roomName = room.getHotelRoom().getHotelRoomName();
                    Long roomNum = room.getRoomNum();
                    String title;
                    String content;
                    
                    if (activeSurvey != null) {
                        title = activeSurvey.getSurveyTitle();
                        content = activeSurvey.getSurveyContent();
                        log.info("회사({})의 활성화된 설문조사 템플릿을 사용합니다.", 
                                room.getHotelRoom().getCompany().getCompanyName());
                    } else {
                        // 활성화된 설문조사가 없는 경우 기본 템플릿 사용
                        title = DEFAULT_SURVEY_TITLE;
                        content = DEFAULT_SURVEY_CONTENT;
                        log.info("회사({})에 활성화된 설문조사가 없어 기본 템플릿을 사용합니다.", 
                                room.getHotelRoom().getCompany().getCompanyName());
                    }
                    
                    try {
                        // 모든 필요한 정보를 이메일 서비스로 전달
                        emailService.sendSurveyEmail(title, content, memberEmail, memberName, roomName, roomNum, room);
                        log.info("설문조사 이메일 전송 완료: {} ({}) - 방: {}, 회사: {}", 
                                memberName, memberEmail, roomName, room.getHotelRoom().getCompany().getCompanyName());
                    } catch (Exception e) {
                        log.error("설문조사 이메일 발송 중 오류 발생 ({}): {}", memberEmail, e.getMessage());
                    }
                }
            }
            
            log.info("설문조사 이메일 발송 완료");
        } catch (Exception e) {
            log.error("설문조사 이메일 발송 중 오류 발생: {}", e.getMessage());
        }
    }
} 