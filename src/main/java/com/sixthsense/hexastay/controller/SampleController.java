/***********************************************
 * 클래스명 : SampleController
 * 기능 : 샘플 컨트롤러입니다.
 * 작성자 : 김예령
 * 작성일 : 2025-03-27
 * 수정 : 2025-03-27 샘플컨트롤러생성,김예령
 * ***********************************************/
package com.sixthsense.hexastay.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.Locale;

@Controller
@RequiredArgsConstructor
@Log4j2
public class SampleController {

    @GetMapping("/main")
    public String main(@PageableDefault(page = 0) Pageable pageable, Model model,
                       @Param("hotelRoomNum") Long hotelRoomNum,
                       Principal principal,
                       Locale locale) { //
//        log.info("main 진입 : " + pageable);
//        log.info("다국어 메인 컨트롤러 진입: {}", locale);

//        Page<SampleDTO> main = sampleService.sampleMethod(pageable);
//        log.info("main 1 : " + pageable);
//        model.addAttribute("main", main);
//        log.info("main 2 : " + pageable);
        log.info("현재 로그인한 사용자: " + (principal != null ? principal.getName() : "없음"));

        model.addAttribute("currentLang", locale.getLanguage());
        log.info("Passing language code to view: {}", locale.getLanguage());

        return "sample/main"; // templates/sample/main.html
    }


}
