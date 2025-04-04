package com.sixthsense.hexastay.controller;
import com.sixthsense.hexastay.dto.QaDTO;
import com.sixthsense.hexastay.service.QaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/qa")
public class QaController {
    private final QaService qaService;
    private final ModelMapper modelMapper = new ModelMapper();
    // 공지사항 목록
    @GetMapping("/list") // (수정)
    public String list(
            Pageable pageable,
            @RequestParam(value = "type",defaultValue = "") String type,
            @RequestParam(value = "keyword",defaultValue = "") String keyword,
            Model model, Principal principal) {
        log.info("공지사항 목록 진입");

        // 서비스 연동
        Page<QaDTO> listDTOS = qaService.qaList(pageable, principal, type, keyword);

        // 값 전달(model)
        model.addAttribute("list", listDTOS);
        model.addAttribute("type", type);
        model.addAttribute("keyword", keyword);

        log.info("공지사항 목록 로드 완료");
        return "/qa/list"; // (수정)
    }
    //등록페이지
    @GetMapping("/insert") // (수정)
    public String insert(Model model, Principal principal) {
        log.info("공지사항 작성 페이지");

        if (principal != null) {
            model.addAttribute("memberNum", principal.getName()); // (수정)
        }

        return "/qa/insert"; // (수정)
    }
    // 공지사항 등록 처리
    @PostMapping("/insert") // (수정)
    public String insertPost(QaDTO qaDTO,Principal principal) {
        log.info("공지사항 등록 요청: " + qaDTO);
        try {
            qaService.qaInsert(qaDTO);
        } catch(Exception e) {
            log.error("공지사항 등록 실패", e);
            return "redirect:/qa/insert?error=true"; // (예: 에러 메시지 전달)
        }
        log.info("요청완료");
        return "redirect:/qa/list"; // (수정)
    }
    //읽기
    @GetMapping("/read/{qaNum}")
    public String read(@PathVariable(name = "qaNum") Long qaNum, Model model){
        log.info("읽기 진입");
        //서비스처리
        QaDTO qaDTO = qaService.qaRead(qaNum);
        model.addAttribute("qadata", qaDTO);
        log.info("읽기 완료");
        return "/qa/read";
    }
//    @GetMapping("/modify/{qaNum}")
//    public String modify(@PathVariable(name = "qaNum") Long qaNum, Model model){
//        log.info("수정 진입");
//        //서비스처리
//        QaDTO qaDTO = qaService.qaRead(qaNum);
//        model.addAttribute("qadata", qaDTO);
//        return "/qa/modify";
//    }
//    @PostMapping("/modify") // (수정)
//    public String modifyPost(QaDTO qaDTO,Principal principal) {
//        qaService.qaModify(qaDTO);
//        log.info("수정완료" + qaDTO);
//        return "redirect:/qa/read/" + qaDTO.getQaNum();
//    }
    @GetMapping("/delete/{qaNum}") // ✅ (추가)
    public String delete(@PathVariable Long qaNum) {
        qaService.qaDelete(qaNum);
        return "redirect:/qa/list";
    }

}
