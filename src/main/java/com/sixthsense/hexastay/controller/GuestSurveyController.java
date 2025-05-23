package com.sixthsense.hexastay.controller;

import com.sixthsense.hexastay.entity.Member;
import com.sixthsense.hexastay.entity.Room;
import com.sixthsense.hexastay.entity.Survey;
import com.sixthsense.hexastay.entity.SurveyResult;
import com.sixthsense.hexastay.repository.MemberRepository;
import com.sixthsense.hexastay.repository.RoomRepository;
import com.sixthsense.hexastay.repository.SurveyResultRepository;
import com.sixthsense.hexastay.service.SurveyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/guest-survey")
public class GuestSurveyController {
    private final SurveyService surveyService;
    private final MemberRepository memberRepository;
    private final RoomRepository roomRepository;
    private final SurveyResultRepository surveyResultRepository;

    @GetMapping("/participate/{id}")
    public String participateSurvey(@PathVariable("id") Long id, 
                                  @RequestParam String memberEmail,
                                  @RequestParam Long roomNum,
                                  @RequestParam(required = false) String checkOutDateStr,
                                  Model model) {
        // ID 없이 room 정보만으로 처리하는 새 메서드로 포워딩
        return participateSurveyWithoutId(memberEmail, roomNum, checkOutDateStr, model);
    }
    
    @GetMapping("/participate")
    public String participateSurveyWithoutId(@RequestParam String memberEmail,
                                  @RequestParam Long roomNum,
                                  @RequestParam(required = false) String checkOutDateStr,
                                  Model model) {
        // 객실 정보 조회
        Room room = roomRepository.findById(roomNum).orElse(null);
        if (room == null) {
            return "redirect:/guest-survey/error";
        }
        
        // 기본 설문조사 객체 생성
        Survey survey = new Survey();
        survey.setSurveyNum(0L); // 임시 ID
        survey.setSurveyTitle("고객님의 소중한 의견을 기다립니다");
        survey.setSurveyContent("더 나은 서비스를 제공하기 위해 고객님의 의견을 듣고자 합니다.\n간단한 설문에 참여해 주시면 큰 도움이 됩니다. 소요 시간은 약 1분입니다.");
        survey.setSurveyIsActive(true);
        
        // 회사에 활성화된 설문조사가 있으면 정보 가져오기
        if (room.getHotelRoom() != null && room.getHotelRoom().getCompany() != null) {
            Long companyNum = room.getHotelRoom().getCompany().getCompanyNum();
            Survey activeSurvey = surveyService.getActiveSurveyByCompany(companyNum);
            if (activeSurvey != null) {
                survey.setSurveyTitle(activeSurvey.getSurveyTitle());
                survey.setSurveyContent(activeSurvey.getSurveyContent());
                survey.setSurveyNum(activeSurvey.getSurveyNum());
            }
        }
        
        // 체크아웃 날짜 파싱
        LocalDate checkOutDate = null;
        if (checkOutDateStr != null && !checkOutDateStr.isEmpty()) {
            try {
                checkOutDate = LocalDate.parse(checkOutDateStr);
            } catch (Exception e) {
                log.warn("체크아웃 날짜 파싱 실패: {}", checkOutDateStr);
            }
        }
        
        // 체크아웃 날짜가 없는 경우 객실에서 가져옴
        if (checkOutDate == null && room.getCheckOutDate() != null) {
            checkOutDate = room.getCheckOutDate().toLocalDate();
        }
        
        // 이미 참여했는지 확인
        boolean hasParticipated = false;
        if (checkOutDate != null) {
            // 체크아웃 날짜가 있는 경우 더 정확한 검사
            hasParticipated = surveyService.hasParticipatedWithCheckOutDate(memberEmail, roomNum, checkOutDate);
        } else {
            // 체크아웃 날짜가 없는 경우 기존 방식으로 검사 (survey ID 불필요)
            hasParticipated = false;
        }
        
        if (hasParticipated) {
            return "redirect:/guest-survey/already-participated";
        }
        
        // 회원 정보 조회
        Member member = memberRepository.findByMemberEmail(memberEmail);
        if (member != null) {
            model.addAttribute("memberName", member.getMemberName());
            model.addAttribute("memberNum", member.getMemberNum());
        } else {
            model.addAttribute("memberName", "고객");
            model.addAttribute("memberNum", null);
        }
        
        model.addAttribute("survey", survey);
        model.addAttribute("memberEmail", memberEmail);
        model.addAttribute("roomNum", roomNum);
        model.addAttribute("checkOutDate", checkOutDate);
        return "survey/survey-participate";
    }

    @PostMapping("/submit")
    public String submitSurvey(@ModelAttribute SurveyResult surveyResult,
                         @RequestParam Long roomNum,
                         @RequestParam String memberEmail,
                         @RequestParam(required = false) String checkOutDate,
                         Model model) {
        try {
            // 회원 정보 조회
            Member member = memberRepository.findByMemberEmail(memberEmail);
            
            // 객실 정보 조회
            Room room = roomRepository.findById(roomNum)
                    .orElseThrow(() -> new RuntimeException("객실 정보를 찾을 수 없습니다."));
            
            // 설문 결과 저장
            surveyResult.setMember(member);
            surveyResult.setRoom(room);
            surveyResult.setMemberEmail(memberEmail);
            surveyResult.setSurveyResultSubmittedAt(LocalDateTime.now());
            
            surveyResultRepository.save(surveyResult);
            
            return "redirect:/guest-survey/complete";
        } catch (Exception e) {
            log.error("설문 제출 오류: ", e);
            model.addAttribute("error", "설문 제출 중 오류가 발생했습니다.");
            return "error/400";
        }
    }

    @GetMapping("/already-participated")
    public String alreadyParticipated() {
        return "survey/already-participated";
    }

    @GetMapping("/error")
    public String error() {
        return "survey/error";
    }

    @GetMapping("/complete")
    public String complete() {
        return "survey/complete";
    }
} 