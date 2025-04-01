package com.sixthsense.hexastay.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class AdminController {

    @GetMapping("/list")
    public String list() {
        return "admin/list";
    }

    @GetMapping("/approve")
    public String approve() {
        return "admin/approve";
    }

    @GetMapping("/insert")
    public String insert() {
        return "admin/insert";
    }

    @GetMapping("/mypage")
    public String mypage() {
        return "admin/mypage";
    }

    @GetMapping("/insertcompany")
    public String insertcompany() {
        return "admin/insertcompany";
    }

}
