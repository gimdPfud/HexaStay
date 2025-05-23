package com.sixthsense.hexastay.controller;

import com.sixthsense.hexastay.dto.MonthlyResultDTO;
import com.sixthsense.hexastay.entity.Company;
import com.sixthsense.hexastay.entity.Survey;
import com.sixthsense.hexastay.entity.SurveyResult;
import com.sixthsense.hexastay.repository.*;
import com.sixthsense.hexastay.scheduler.SurveyEmailScheduler;
import com.sixthsense.hexastay.service.AdminService;
import com.sixthsense.hexastay.service.MemberService;
import com.sixthsense.hexastay.service.SurveyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
    public String list(Model model, Principal principal) {
        // 로그인한 사용자의 회사 정보 가져오기
        String email = principal.getName();
        Long companyNum = adminService.adminFindEmail(email).getCompanyNum();
        
        // 회사별 설문조사 목록 조회
        model.addAttribute("surveys", surveyService.getSurveysByCompany(companyNum));
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

        // 참여자 수만 계산
        int participantCount = responses.size();

        model.addAttribute("survey", survey);
        model.addAttribute("responses", responses);
        model.addAttribute("participantCount", participantCount);

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

    //todo:경로 오타 추후에 수정
    @GetMapping("/suyveytest")
    @ResponseBody
    public ResponseEntity<String> surveytest() {
        surveyEmailScheduler.sendSurveyEmails();
        return ResponseEntity.ok("설문 이메일이 성공적으로 전송되었습니다.");
    }

    // 설문조사 활성화/비활성화
    @PostMapping("/toggle-active/{id}")
    @ResponseBody
    public ResponseEntity<String> toggleActive(@PathVariable Long id, Principal principal) {
        try {
            // 로그인한 사용자의 회사 정보 가져오기
            String email = principal.getName();
            Long companyNum = adminService.adminFindEmail(email).getCompanyNum();
            
            // 활성화할 설문 조회
            Survey survey = surveyService.getSurveyById(id);
            
            // 설문이 해당 회사 소속인지 확인
            if (survey == null || survey.getCompany() == null || !survey.getCompany().getCompanyNum().equals(companyNum)) {
                return ResponseEntity.badRequest().body("권한이 없습니다.");
            }
            
            // 회사 소속의 모든 설문 조회
            List<Survey> allCompanySurveys = surveyRepository.findByCompany_CompanyNum(companyNum);
            
            // 모든 설문을 비활성화
            for (Survey s : allCompanySurveys) {
                s.setSurveyIsActive(false);
                surveyRepository.save(s);
            }
            
            // 선택한 설문만 활성화
            survey.setSurveyIsActive(true);
            surveyRepository.save(survey);
            
            return ResponseEntity.ok("설문조사 활성화 상태가 변경되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("설문조사 활성화 상태 변경 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}
