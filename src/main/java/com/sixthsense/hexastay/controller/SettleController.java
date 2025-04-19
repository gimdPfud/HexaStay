package com.sixthsense.hexastay.controller;

import com.sixthsense.hexastay.entity.Company;
import com.sixthsense.hexastay.service.AdminService;
import com.sixthsense.hexastay.service.CompanyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/settle")
public class SettleController {

    private final AdminService adminService;
    private final CompanyService companyService;

    @GetMapping("/chart")
    public String chart(Principal principal, Pageable pageable)
    {

        Long companyNum = adminService.adminFindEmail(principal.getName()).getCompanyNum();
        if (companyNum != null) {
            List<Company> companyList = companyService.getCompanyList(companyNum);
        } else {

        }

        return "/settle/chart";
    }

    @GetMapping("/salaries")
    public String salaries() {
        return "/settle/salaries";
    }



}
