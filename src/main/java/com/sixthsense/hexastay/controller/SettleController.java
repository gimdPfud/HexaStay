package com.sixthsense.hexastay.controller;

import com.sixthsense.hexastay.dto.CompanyDTO;
import com.sixthsense.hexastay.dto.StoreDTO;
import com.sixthsense.hexastay.entity.Company;
import com.sixthsense.hexastay.service.AdminService;
import com.sixthsense.hexastay.service.CompanyService;
import com.sixthsense.hexastay.service.SettleService;
import com.sixthsense.hexastay.service.StoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
    private final SettleService settleService;
    private final StoreService storeService;

    @GetMapping("/chart")
    public String chart(Principal principal, Model model)
    {

        Long companyNum = adminService.adminFindEmail(principal.getName()).getCompanyNum();
        Long storeNum = adminService.adminFindEmail(principal.getName()).getStoreNum();

        String role = adminService.adminFindEmail(principal.getName()).getAdminRole();
        if (role.equals("admin")) {
            List<CompanyDTO> companyList = settleService.getCompanyParent(companyNum);
            model.addAttribute("companyList", companyList);
            List<StoreDTO> storeList = storeService.list(companyNum);
            model.addAttribute("storeList", storeList);
        }

        else if (companyNum != null) {
            List<CompanyDTO> companyList = adminService.getCompanyParent(companyNum);
            model.addAttribute("companyList", companyList);
        } else {
            List<StoreDTO> storeList = storeService.list(companyNum);
            model.addAttribute("storeList", storeList);
        }


        return "/settle/chart";
    }

    @GetMapping("/salaries")
    public String salaries() {
        return "/settle/salaries";
    }



}
