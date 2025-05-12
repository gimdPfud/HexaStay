package com.sixthsense.hexastay.controller;

import com.sixthsense.hexastay.dto.*;
import com.sixthsense.hexastay.entity.Admin;
import com.sixthsense.hexastay.entity.Company;
import com.sixthsense.hexastay.entity.Member;
import com.sixthsense.hexastay.entity.Room;
import com.sixthsense.hexastay.entity.Survey;
import com.sixthsense.hexastay.entity.SurveyResult;
import com.sixthsense.hexastay.repository.AdminRepository;
import com.sixthsense.hexastay.repository.CompanyRepository;
import com.sixthsense.hexastay.repository.MemberRepository;
import com.sixthsense.hexastay.repository.RoomRepository;
import com.sixthsense.hexastay.repository.StoreRepository;
import com.sixthsense.hexastay.repository.SurveyRepository;
import com.sixthsense.hexastay.repository.SurveyResultRepository;
import com.sixthsense.hexastay.scheduler.SurveyEmailScheduler;
import com.sixthsense.hexastay.service.*;
import com.sixthsense.hexastay.service.impl.EmailServiceImpl;
import com.sixthsense.hexastay.service.impl.MemberServiceImpl;
import jakarta.persistence.EntityNotFoundException;
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
    private final SurveyEmailScheduler surveyEmailScheduler;
    private final RoomRepository roomRepository;
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final SurveyResultRepository surveyResultRepository;
    private final SurveyRepository surveyRepository;
    private final AdminService adminService;
    private final CompanyRepository companyRepository;

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
    public String save(@ModelAttribute Survey survey, Principal principal) {
        // 로그인한 사용자의 회사 정보 세팅
        String email = principal.getName();
        Long companyNum = adminService.adminFindEmail(email).getCompanyNum();
        Company company = companyRepository.findById(companyNum).orElseThrow();
        survey.setCompany(company);
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
    public String surveyResult(@PathVariable Long id, Model model, Principal principal) {
        // 로그인한 사용자의 회사번호 찾기
        String email = principal.getName();
        Long companyNum = adminService.adminFindEmail(email).getCompanyNum();

        Survey survey = surveyService.getSurveyById(id);
        // 설문이 해당 회사 소속인지 체크
        if (survey == null || survey.getCompany() == null || !survey.getCompany().getCompanyNum().equals(companyNum)) {
            return "error/400";
        }

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

    // 설문조사 결과 목록 페이지 - 월별 그룹핑
    @GetMapping("/results")
    public String resultsList(Model model, 
                             @RequestParam(required = false) Integer year,
                             @RequestParam(required = false) Integer month) {
        List<SurveyResult> allResults = surveyResultRepository.findAll();
        
        // 연도 목록 (중복 제거)
        List<Integer> years = allResults.stream()
                .map(r -> r.getSurveyResultSubmittedAt().getYear())
                .distinct()
                .sorted()
                .toList();
        
        // 필터링할 연도와 월이 있으면 적용
        List<SurveyResult> filteredResults = allResults;
        if (year != null) {
            filteredResults = filteredResults.stream()
                    .filter(r -> r.getSurveyResultSubmittedAt().getYear() == year)
                    .toList();
            
            if (month != null) {
                filteredResults = filteredResults.stream()
                        .filter(r -> r.getSurveyResultSubmittedAt().getMonthValue() == month)
                        .toList();
            }
        } else if (month != null) {
            filteredResults = filteredResults.stream()
                    .filter(r -> r.getSurveyResultSubmittedAt().getMonthValue() == month)
                    .toList();
        }
        
        // 월별로 그룹핑하여 MonthlyResultDTO 리스트 생성
        Map<Integer, Map<Integer, Long>> groupedByYearAndMonth = filteredResults.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        r -> r.getSurveyResultSubmittedAt().getYear(),
                        java.util.stream.Collectors.groupingBy(
                                r -> r.getSurveyResultSubmittedAt().getMonthValue(),
                                java.util.stream.Collectors.counting()
                        )
                ));
        
        List<MonthlyResultDTO> monthlyResults = new java.util.ArrayList<>();
        
        groupedByYearAndMonth.forEach((y, monthMap) -> {
            monthMap.forEach((m, count) -> {
                monthlyResults.add(new MonthlyResultDTO(y, m, count));
            });
        });
        
        // 연도-월 내림차순 정렬
        monthlyResults.sort((a, b) -> {
            int yearCompare = Integer.compare(b.getYear(), a.getYear());
            if (yearCompare == 0) {
                return Integer.compare(b.getMonth(), a.getMonth());
            }
            return yearCompare;
        });
        
        model.addAttribute("surveyResults", monthlyResults);
        model.addAttribute("years", years);
        model.addAttribute("selectedYear", year);
        model.addAttribute("selectedMonth", month);
        model.addAttribute("searchMonth", year != null && month != null ? 
                String.format("%04d-%02d", year, month) : "");
        
        return "survey/surveyresultlist";
    }
    
    // 월별 설문조사 결과 상세 페이지
    @GetMapping("/results/month/{yearMonth}")
    public String monthlyResults(@PathVariable String yearMonth, Model model, Principal principal) {
        try {
            // yearMonth 형식: "2024-05"
            String[] parts = yearMonth.split("-");
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            
            // 로그인한 사용자의 회사번호 찾기
            String email = principal.getName();
            Long companyNum = adminService.adminFindEmail(email).getCompanyNum();
            
            // 해당 월의 설문 응답 조회
            LocalDateTime startDate = LocalDateTime.of(year, month, 1, 0, 0);
            LocalDateTime endDate = startDate.plusMonths(1).minusSeconds(1);
            
            List<SurveyResult> responses = surveyResultRepository.findBySurvey_Company_CompanyNumAndSurveyResultSubmittedAtBetween(
                    companyNum, startDate, endDate);
            
            if (responses.isEmpty()) {
                model.addAttribute("responses", Collections.emptyList());
                model.addAttribute("participantCount", 0);
                return "survey/surveyresult";
            }
            
            // 참여자 수
            int participantCount = responses.size();
            
            // 기준 설문(설명용, 첫 응답의 설문으로 설정)
            Survey referenceSurvey = responses.get(0).getSurvey();
            
            model.addAttribute("survey", referenceSurvey);
            model.addAttribute("responses", responses);
            model.addAttribute("participantCount", participantCount);
            model.addAttribute("yearMonth", yearMonth);
            
            return "survey/surveyresult";
            
        } catch (Exception e) {
            log.error("월별 설문 결과 조회 오류: " + e.getMessage(), e);
            return "error/400";
        }
    }

    @GetMapping("/suyveytest")
    @ResponseBody
    public ResponseEntity<String> surveytest() {
        surveyEmailScheduler.sendSurveyEmails();
        return ResponseEntity.ok("설문 이메일이 성공적으로 전송되었습니다.");
    }

    @GetMapping("/participate/{id}")
    public String participateSurvey(@PathVariable("id") Long id, 
                                  @RequestParam String memberEmail,
                                  @RequestParam Long roomNum,
                                  Model model) {
        Survey survey = surveyService.getSurveyById(id);
        if (survey == null) {
            return "redirect:/survey/list";
        }
        
        // 이미 참여했는지 확인
        if (surveyService.hasParticipated(id, memberEmail)) {
            return "redirect:/survey/already-participated";
        }
        
        model.addAttribute("survey", survey);
        model.addAttribute("memberEmail", memberEmail);
        model.addAttribute("roomNum", roomNum);
        return "survey/survey-participate";
    }

    @PostMapping("/submit")
    public String submitSurvey(@ModelAttribute SurveyResult surveyResult,
                         @RequestParam Long roomNum,
                         @RequestParam String memberEmail,
                         Model model) {
        try {
            // 회원 정보 조회
            Member member = memberRepository.findByMemberEmail(memberEmail);
            if (member == null) {
                throw new RuntimeException("회원을 찾을 수 없습니다.");
            }
            
            // 객실 정보 조회
            Room room = roomRepository.findById(roomNum)
                    .orElseThrow(() -> new RuntimeException("객실 정보를 찾을 수 없습니다."));
            
            // 설문 결과 저장
            surveyResult.setMember(member);
            surveyResult.setRoom(room);
            surveyResult.setSurveyResultSubmittedAt(LocalDateTime.now());
            
            surveyResultRepository.save(surveyResult);
            
            return "redirect:/survey/complete";
        } catch (Exception e) {
            model.addAttribute("error", "설문 제출 중 오류가 발생했습니다.");
            return "error/400";
        }
    }

    @GetMapping("/already-participated")
    public String alreadyParticipated() {
        return "survey/already-participated";
    }

    @GetMapping("/thank-you")
    public String thankYou() {
        return "survey/thank-you";
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
