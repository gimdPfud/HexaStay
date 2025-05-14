package com.sixthsense.hexastay.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@Log4j2
public class tossController {

    // 토스 연동
    @RequestMapping(value = "/payment", method = {RequestMethod.GET, RequestMethod.POST})
    public String tossControl (@RequestParam Integer tossprice, Model model) {
        log.info("히히 주문 토스");
        model.addAttribute("tossprice", tossprice);
        return "toss/payment";
    }

    @GetMapping("/success")
    public String successPage(@RequestParam Integer tossprice, Model model) {
        model.addAttribute("tossprice", tossprice);
        return "toss/success";
    }

    @GetMapping("/fail")
    public String failPage() {
        return "toss/fail";
    }

    @GetMapping("/tossloading")
    public String tossloadingPage() {
        return "toss/tossloading";
    }

    @RequestMapping(value = "/storepayment", method = {RequestMethod.GET, RequestMethod.POST})
    public String storetossControl (@RequestParam Integer tossprice, Model model) {
        log.info("히히 주문 토스 store");
        model.addAttribute("tossprice", tossprice);
        return "toss/store/payment";
    }

    @GetMapping("/storesuccess")
    public String storesuccessPage(@RequestParam Integer tossprice, Model model) {
        log.info("스토어성공?");
        model.addAttribute("tossprice", tossprice);
        return "toss/store/success";
    }
}

