package com.sixthsense.hexastay.controller;

import com.sixthsense.hexastay.dto.AdminDTO;
import com.sixthsense.hexastay.dto.CompanyDTO;
import com.sixthsense.hexastay.dto.StoreDTO;
import com.sixthsense.hexastay.entity.Admin;
import com.sixthsense.hexastay.entity.Room;
import com.sixthsense.hexastay.entity.Survey;
import com.sixthsense.hexastay.entity.SurveyResult;
import com.sixthsense.hexastay.repository.AdminRepository;
import com.sixthsense.hexastay.repository.CompanyRepository;
import com.sixthsense.hexastay.repository.RoomRepository;
import com.sixthsense.hexastay.repository.StoreRepository;
import com.sixthsense.hexastay.service.AdminService;
import com.sixthsense.hexastay.service.CompanyService;
import com.sixthsense.hexastay.service.EmailService;
import com.sixthsense.hexastay.service.SurveyService;
import com.sixthsense.hexastay.service.impl.EmailServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/survey")
public class SurveyController {
    private final SurveyService surveyService;
    private final EmailService emailService;

    private RoomRepository roomRepository;

    // 설문조사 목록 페이지
    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("surveys", surveyService.getAllSurveys());
        return "survey/surveylist";
    }

    // 설문조사 작성 페이지
    @GetMapping("/form")
    public String form(Model model) {
        model.addAttribute("survey", new Survey());
        return "survey/surveyform";
    }

    // 설문조사 저장
    @PostMapping("/save")
    public String save(@ModelAttribute Survey survey) {
        surveyService.saveSurvey(survey);
        return "redirect:/survey/list";
    }

    // 설문조사 수정 페이지
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("survey", surveyService.getSurveyById(id));
        return "survey/surveyform";
    }

    // 설문조사 삭제
    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public ResponseEntity<String> delete(@PathVariable Long id) {
        try {
            surveyService.deleteSurvey(id);
            return ResponseEntity.ok("설문조사가 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("설문조사 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 설문조사 결과 페이지
    @GetMapping("/results/{id}")
    public String surveyResult(@PathVariable Long id, Model model) {
        Survey survey = surveyService.getSurveyById(id);
        List<SurveyResult> responses = surveyService.getSurveyResults(id);
        
        // 통계 데이터 계산
        int participantCount = responses.size();
        double averageRating = responses.stream()
                .mapToDouble(r -> (r.getSurveyResultCleanliness() + r.getSurveyResultStaff() + 
                                 r.getSurveyResultCheckInOut() + r.getSurveyResultFacility() + 
                                 r.getSurveyResultFood() + r.getSurveyResultValue()) / 6.0)
                .average()
                .orElse(0.0);
                
        double cleanlinessAvg = responses.stream()
                .mapToDouble(SurveyResult::getSurveyResultCleanliness)
                .average()
                .orElse(0.0);
                
        double staffAvg = responses.stream()
                .mapToDouble(SurveyResult::getSurveyResultStaff)
                .average()
                .orElse(0.0);
                
        double checkInOutAvg = responses.stream()
                .mapToDouble(SurveyResult::getSurveyResultCheckInOut)
                .average()
                .orElse(0.0);
                
        double facilityAvg = responses.stream()
                .mapToDouble(SurveyResult::getSurveyResultFacility)
                .average()
                .orElse(0.0);
                
        double foodAvg = responses.stream()
                .mapToDouble(SurveyResult::getSurveyResultFood)
                .average()
                .orElse(0.0);
                
        double valueAvg = responses.stream()
                .mapToDouble(SurveyResult::getSurveyResultValue)
                .average()
                .orElse(0.0);
        
        model.addAttribute("survey", survey);
        model.addAttribute("responses", responses);
        model.addAttribute("participantCount", participantCount);
        model.addAttribute("averageRating", averageRating);
        model.addAttribute("cleanlinessAvg", cleanlinessAvg);
        model.addAttribute("staffAvg", staffAvg);
        model.addAttribute("checkInOutAvg", checkInOutAvg);
        model.addAttribute("facilityAvg", facilityAvg);
        model.addAttribute("foodAvg", foodAvg);
        model.addAttribute("valueAvg", valueAvg);
        
        return "survey/surveyresult";
    }

    // 설문조사 결과 목록 페이지
    @GetMapping("/results")
    public String resultsList(Model model) {
        List<Survey> surveys = surveyService.getAllSurveys();
        model.addAttribute("surveys", surveys);
        return "survey/surveyresultlist";
    }

    @GetMapping("/suyveytest")
    public void surveytest() {
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
