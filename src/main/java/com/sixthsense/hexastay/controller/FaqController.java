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
    public String faqInsertForm(Model model) {
        log.info("FAQ 등록 폼 진입");
        model.addAttribute("faqDTO", new FaqDTO()); // 폼 바인딩용
        return "faq/insert";
    }

    // (3) FAQ 등록 처리
    @PostMapping("/insert")
    public String faqInsert(@ModelAttribute FaqDTO faqDTO, Principal principal) {
        log.info("FAQ 등록 요청 수신 - 제목: {}", faqDTO.getFaqTitle());
        faqService.faqInsert(faqDTO, principal);
        return "redirect:/faq/list";
    }

    // (4) FAQ 수정 처리 (AJAX/모달 또는 POST 방식)
    @PostMapping("/modify")
    public String faqModify(@ModelAttribute FaqDTO faqDTO) {
        log.info("FAQ 수정 요청 - 번호: {}, 제목: {}", faqDTO.getFaqNum(), faqDTO.getFaqTitle());
        faqService.faqModify(faqDTO);
        return "redirect:/faq/list";
    }

    // (5) FAQ 삭제 처리
    @PostMapping("/delete")
    public String faqDelete(@RequestParam("faqNum") Long faqNum) {
        log.info("FAQ 삭제 요청 - 번호: {}", faqNum);
        faqService.faqDelete(faqNum);
        return "redirect:/faq/list";
    }
}
