package com.sixthsense.hexastay.controller;

import com.sixthsense.hexastay.dto.CenterDTO;
import com.sixthsense.hexastay.service.AdminService;
import com.sixthsense.hexastay.service.CenterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
public class AdminController {

    private final AdminService adminService;
    private final CenterService centerService;

    @GetMapping("/list")
    public String list() {
        return "admin/list";
    }

    @GetMapping("/approve")
    public String approve() {
        return "admin/approve";
    }

    @GetMapping("/insert")
    public String insert(Model model) {
        List<CenterDTO> centerDTOList = centerService.allCenterList();
        model.addAttribute("centerDTOList", centerDTOList);
                for (CenterDTO centers : centerDTOList) {
                    log.info("뭐지?" +centers);
        }

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

    @GetMapping("/searchbranch")
    public String insertbranch(@RequestParam String branchName) {
        adminService.getBranchList(branchName);
        return "admin/insertcompany";
    }

    @GetMapping("/searchfacility")
    public String insertfacility(@RequestParam String facilityName) {
        adminService.getFacilityList(facilityName);
        return "admin/insertcompany";
    }
}
