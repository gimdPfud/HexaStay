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
    public String listCompany(@RequestParam(required = false) String choice,
                              @RequestParam(required = false) String select,
                              @RequestParam(required = false) String keyword,
                              Model model,
                              Pageable pageable) {

        log.info("choice : " + choice);
        log.info("select : " + select);
        log.info("keyword : " + keyword);
        log.info("pageable : " + pageable.getPageNumber(), pageable.getPageSize());

        if (choice == null || choice.trim().isEmpty()) {
            choice = "center";
        }

        Page<CompanyDTO> companyDTOS = companyService.companySearchList(select, choice, keyword, pageable);

        model.addAttribute("companyDTOS", companyDTOS);
        model.addAttribute("choice", choice);
        model.addAttribute("select", select);
        model.addAttribute("keyword", keyword);

        return "company/list";
    }

    @PostMapping("/list")
    public String listPost (@RequestParam("select") String select, @RequestParam("choice") String choice, @RequestParam ("keyword") String keyword) {


        return "company/list";
    }

    @GetMapping("/insert")
    public String insertCompanyGet(Model model){

        model.addAttribute("company", companyService.companyList());

        return "company/insert";
    }

    @PostMapping("/insert")
    public String insertCompanyPost(CompanyDTO companyDTO) throws IOException {

        companyService.companyInsert(companyDTO);

        return "redirect:/company/list";
    }

    @GetMapping("/read/{companyNum}")
    public String readCompany(@PathVariable(name = "companyNum") Long companyNum, Model model) {
        CompanyDTO companyDTO = companyService.companyRead(companyNum);

        log.info("read Controller 진입 헤헤헤헤헤");
        log.info("companyNum 갖고 옴?: " + companyNum);

        if (companyDTO == null) {
            log.info("companyDTO가 null인 pk : " + companyNum);
            return "redirect:/company/list";
        }
        model.addAttribute("companyDTO", companyDTO);
        return "company/read";
    }

    @GetMapping("/modify/{companyNum}")
    public String modifyCompanyGet(Model model, @PathVariable(name = "companyNum") Long companyNum) {

        CompanyDTO companyDTO = companyService.companyRead(companyNum);
        model.addAttribute("companyDTO", companyDTO);

        return "company/modify";
    }

    @PostMapping("/modify")
    public String modifyCompanyPost(CompanyDTO companyDTO) throws IOException {

        if (companyDTO.getCompanyPicture().isEmpty()) {
            if (!companyService.companyRead(companyDTO.getCompanyNum()).getCompanyPictureMeta().isEmpty())
            companyDTO.setCompanyPictureMeta(companyService.companyRead(companyDTO.getCompanyNum()).getCompanyPictureMeta());
    }
        companyService.companyModify(companyDTO);

        return "redirect:/company/list";
    }

    @PostMapping("/deactivate/{companyNum}")
    public String deactivateCompany(@PathVariable(name = "companyNum") Long companyNum) throws IOException {

        companyService.deactivateCompany(companyNum);

        return "redirect:/company/read/" + companyNum;
    }

    @PostMapping("/activate/{companyNum}")
    public String activateCompany(@PathVariable(name = "companyNum") Long companyNum) {
        companyService.activateCompany(companyNum); // 내부에서 status를 ACTIVE로 변경
        return "redirect:/company/read/" + companyNum;
    }


}
