/***********************************************
 * 클래스명 : SampleController
 * 기능 : 샘플 컨트롤러입니다.
 * 작성자 : 김예령
 * 작성일 : 2025-03-27
 * 수정 : 2025-03-27 샘플컨트롤러생성,김예령
 * ***********************************************/
package com.sixthsense.hexastay.controller;

import com.sixthsense.hexastay.dto.SampleDTO;
import com.sixthsense.hexastay.service.SampleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
@Log4j2
public class SampleController {
    private final SampleService sampleService;

    @GetMapping("/sample")
    public String sample(@PageableDefault(page = 1)Pageable pageable, Model model) {
        log.info("샘플페이지 진입 : "+pageable);
        Page<SampleDTO> list = sampleService.sampleMethod(pageable);
        model.addAttribute("list",list);
        return "sample/list";
    }
}
