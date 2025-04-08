package com.sixthsense.hexastay.controller;
import com.sixthsense.hexastay.dto.FaqDTO;
import com.sixthsense.hexastay.entity.Member;
import com.sixthsense.hexastay.service.FaqService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;
@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/faq")
public class FaqController {

    private final FaqService faqService;

    // (1) FAQ 목록
    @GetMapping("/list")
    public String faqList(@RequestParam(value = "category", required = false, defaultValue = "예약") String category,
                          Model model, Principal principal) {
        log.info("FAQ 목록 - category={}", category);
        List<FaqDTO> faqList = faqService.faqList(category);
        String adminRole = null;
        if (principal != null) {
            adminRole = faqService.AdminRole(principal);
        }

        model.addAttribute("faqList", faqList);
        model.addAttribute("adminRole", adminRole);
        model.addAttribute("category", category); // 선택된 카테고리 기억용

        return "faq/list";
    }


    // (2) FAQ 등록 폼 (관리자만)
    @GetMapping("/insert")
    public String faqInsertForm() {
        log.info("FAQ 등록 폼 진입");
        return "faq/insert";
    }

    // (3) FAQ 등록 처리
    @PostMapping("/insert")
    public String faqInsert(@ModelAttribute FaqDTO faqDTO, Principal principal) {
        log.info("FAQ 등록 요청 수신 - 제목: {}", faqDTO.getFaqTitle());
        try {
            faqService.faqInsert(faqDTO, principal);
        } catch(Exception e) {
            log.error("공지사항 등록 실패", e);
            return "redirect:/faq/insert?error=true"; // (예: 에러 메시지 전달)
        }
        log.info("요청완료");
        return "redirect:/faq/list";
    }
    // (4) FAQ 수정 처리 (AJAX/모달 또는 POST 방식)
    @PostMapping("/modify")
    public String faqModify(@ModelAttribute FaqDTO faqDTO) {
        log.info("FAQ 수정 요청 - 번호: {}, 제목: {}", faqDTO.getFaqNum(), faqDTO.getFaqTitle());
        faqService.faqModify(faqDTO);
        return "redirect:/faq/list" + faqDTO.getFaqNum();
    }

    // (5) FAQ 삭제 처리
    @PostMapping("/delete")
    public String faqDelete(@RequestParam("faqNum") Long faqNum) {
        log.info("FAQ 삭제 요청 - 번호: {}", faqNum);
        faqService.faqDelete(faqNum);
        return "redirect:/faq/list";
    }
}
