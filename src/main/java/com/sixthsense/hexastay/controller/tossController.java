package com.sixthsense.hexastay.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@Log4j2
public class tossController {

    // 토스 연동
    @RequestMapping(value = "/payment", method = {RequestMethod.GET, RequestMethod.POST})
    public String tossControl (@RequestParam Integer tossprice, Model model) {
        log.info("히히 주문 토스");
        model.addAttribute("tossprice", tossprice);
        return "/toss/payment";
    }

    @GetMapping("/success")
    public String successPage(@RequestParam Integer tossprice, Model model) {
        model.addAttribute("tossprice", tossprice);
        return "/toss/success";
    }

    @GetMapping("/fail")
    public String failPage() {
        return "/toss/fail";
    }

    @GetMapping("/tossloading")
    public String tossloadingPage() {
        return "/toss/tossloading";
    }
}

