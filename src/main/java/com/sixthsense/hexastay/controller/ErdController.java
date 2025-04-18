package com.sixthsense.hexastay.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/erd")
public class ErdController {

    @GetMapping("/main")
    public String erd() {
        return "erd";
    }

    @GetMapping("/insert")
    public String insert() {
        return "insert";
    }

    @GetMapping("update")
    public String update() {
        return "update";
    }

    @PostMapping("/insert")
    public String insertPost() {
        return "insert";
    }

    @PostMapping("/delete")
    public String delete() {
        return "delete";
    }

}
