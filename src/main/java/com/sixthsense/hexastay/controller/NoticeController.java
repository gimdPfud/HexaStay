package com.sixthsense.hexastay.controller;
import com.sixthsense.hexastay.dto.NoticeDTO;
import com.sixthsense.hexastay.service.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/notice")
public class NoticeController {
    private final NoticeService noticeService;
    // 공지사항 목록
    @GetMapping("/list") // (수정)
    public String list(
            Pageable pageable,
            @RequestParam(value = "type",defaultValue = "") String type,
            @RequestParam(value = "keyword",defaultValue = "") String keyword,
            Model model, Principal principal) {
        log.info("공지사항 목록 진입");

        // 서비스 연동
        Page<NoticeDTO> listDTOS = noticeService.noticeList(pageable, principal, type, keyword);

        // 값 전달(model)
        model.addAttribute("list", listDTOS);
        model.addAttribute("type", type);
        model.addAttribute("keyword", keyword);

        log.info("공지사항 목록 로드 완료");
        return "/notice/list"; // (수정)
    }
    //등록페이지
    @GetMapping("/insert") // (수정)
    public String insert(Model model, Principal principal) {
        log.info("공지사항 작성 페이지");

        if (principal != null) {
            model.addAttribute("memberNum", principal.getName()); // (수정)
        }

        return "/notice/insert"; // (수정)
    }
    // 공지사항 등록 처리
    @PostMapping("/insert") // (수정)
    public String insertPost(NoticeDTO noticeDTO,Principal principal) {
        log.info("공지사항 등록 요청: " + noticeDTO);
        try {
            noticeService.noticeInsert(noticeDTO);
        } catch(Exception e) {
            log.error("공지사항 등록 실패", e);
            return "redirect:/notice/insert?error=true"; // (예: 에러 메시지 전달)
        }
        log.info("요청완료");
        return "redirect:/notice/list"; // (수정)
    }
    //읽기
    @GetMapping("/read/{noticeNum}")
    public String read(@PathVariable(name = "noticeNum") Long noticeNum, Model model){
        log.info("읽기 진입");
        //서비스처리
        NoticeDTO noticeDTO = noticeService.noticeRead(noticeNum);
        model.addAttribute("noticedata", noticeDTO);
        log.info("읽기 완료");
        return "/notice/read";
    }
    @GetMapping("/modify/{noticeNum}")
    public String modify(@PathVariable(name = "noticeNum") Long noticeNum, Model model){
        log.info("수정 진입");
        //서비스처리
        NoticeDTO noticeDTO = noticeService.noticeRead(noticeNum);
        model.addAttribute("noticedata", noticeDTO);
        return "/notice/modify";
    }
    @PostMapping("/modify") // (수정)
    public String modifyPost(NoticeDTO noticeDTO,Principal principal) {
        noticeService.noticeModify(noticeDTO);
        log.info("수정완료" + noticeDTO);
        return "redirect:/notice/read/" + noticeDTO.getNoticeNum();
    }
    @GetMapping("/delete/{noticeNum}") // ✅ (추가)
    public String delete(@PathVariable Long noticeNum) {
        noticeService.noticeDelete(noticeNum);
        return "redirect:/notice/list";
    }
}
