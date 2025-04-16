package com.sixthsense.hexastay.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/settle")
public class settleController {

    @GetMapping("/main")
    public String main() {
        return "/settle/chart";
    }

    @GetMapping("/salaries")
    public String salaries() {
        return "/settle/salaries";
    }



}
