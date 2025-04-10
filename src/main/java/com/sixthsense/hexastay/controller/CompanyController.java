package com.sixthsense.hexastay.controller;


import com.sixthsense.hexastay.dto.CompanyDTO;
import com.sixthsense.hexastay.service.CompanyService;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
@ToString
@RequestMapping("/company")
public class CompanyController {

    private final CompanyService companyService;

    @GetMapping("/list")
    public String listCenter(Model model, Pageable pageable){

        return "center/list";
    }


    @GetMapping("/signup")
    public String signUpCenterGet(Model model){

        return "center/signup";
    }



    @GetMapping("/{type}/modify/{id}")
    public String modifyOrg(@PathVariable("type") String type,
                            @PathVariable("id") Long id,
                            Model model) {

        return "center/modify";
    }


}
