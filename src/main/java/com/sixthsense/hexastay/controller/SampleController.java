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
import org.springframework.data.repository.query.Param;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;

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
    @GetMapping("/password")
    public String password(@PageableDefault(page = 1)Pageable pageable, Model model) {
        log.info("샘플페이지 진입 : "+pageable);
        Page<SampleDTO> password = sampleService.sampleMethod(pageable);
        model.addAttribute("password",password);
        return "sample/password";
    }
    @GetMapping("/main")
    public String main(@PageableDefault(page = 0) Pageable pageable, Model model,
                       @Param("hotelRoomNum") Long hotelRoomNum,
                       Principal principal) {
        log.info("main 진입 : " + pageable);

        Page<SampleDTO> main = sampleService.sampleMethod(pageable);
        log.info("main 1 : " + pageable);
        model.addAttribute("main", main);
        log.info("main 2 : " + pageable);
        log.info("현재 로그인한 사용자: " + (principal != null ? principal.getName() : "없음"));
        return "sample/main";
    }
    @GetMapping("/admin/store/order/pay")
    public String storeMaechul(){
        return "store/mae";
    }


}
