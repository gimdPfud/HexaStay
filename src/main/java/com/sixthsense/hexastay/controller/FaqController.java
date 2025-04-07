package com.sixthsense.hexastay.controller;
import com.sixthsense.hexastay.dto.FaqDTO;
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

    // FAQ 목록 (토글 형식)
    @GetMapping("/list")
    public String faqList(Model model) {
        log.info("FAQ 목록 페이지 진입"); // (로그 추가)
        List<FaqDTO> faqList = faqService.faqList();
        model.addAttribute("faqList", faqList);
        return "faq/list"; // 토글 형식으로 보여줄 html
    }

    // FAQ 등록 (관리자만 접근 가능)


    @GetMapping("/insert")
    public String faqInsertForm(Model model) {
        log.info("FAQ 등록 폼 진입");
        // 관리자가 입력할 폼을 화면에 제공
        return "faq/insert"; // 등록 폼을 제공하는 JSP 또는 HTML 페이지
    }

    @PostMapping("/insert")
    public String faqInsert(@ModelAttribute FaqDTO faqDTO, Principal principal) {
        log.info("FAQ 등록 요청 수신 - 제목: {}", faqDTO.getFaqTitle()); // (로그 추가)
        faqDTO.setMemberNum(1L);
        faqService.faqInsert(faqDTO, principal);
        log.info("FAQ 등록 완료"); // (로그 추가)
        return "redirect:/faq/list";
    }

    // FAQ 수정 (AJAX 또는 모달 처리용, 관리자만)
    @PostMapping("/modify")
    public String faqModify(@ModelAttribute FaqDTO faqDTO) {
        log.info("FAQ 수정 요청 수신 - 번호: {}, 제목: {}", faqDTO.getFaqNum(), faqDTO.getFaqTitle()); // (로그 추가)
        faqService.faqModify(faqDTO);
        log.info("FAQ 수정 완료"); // (로그 추가)
        return "redirect:/faq/list";
    }

    // FAQ 삭제 (관리자만)
    @PostMapping("/delete")
    public String faqDelete(@RequestParam("faqNum") Long faqNum) {
        log.info("FAQ 삭제 요청 수신 - 번호: {}", faqNum); // (로그 추가)
        faqService.faqDelete(faqNum);
        log.info("FAQ 삭제 완료"); // (로그 추가)
        return "redirect:/faq/list";
    }

}
