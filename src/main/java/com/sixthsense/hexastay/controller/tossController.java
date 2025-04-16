package com.sixthsense.hexastay.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/toss")
public class tossController {

    @GetMapping("/payment")
    public String paymentPage() {
        return "/toss/payment";
    }

    @GetMapping("/success")
    public String successPage() {
        return "/toss/success";
    }

    @GetMapping("/fail")
    public String failPage() {
        return "/toss/fail";
    }
}

