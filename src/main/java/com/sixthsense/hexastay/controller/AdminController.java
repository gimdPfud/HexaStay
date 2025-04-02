package com.sixthsense.hexastay.controller;

import com.sixthsense.hexastay.dto.*;
import com.sixthsense.hexastay.service.AdminService;
import com.sixthsense.hexastay.service.CenterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
        return "admin/insert";
    }

    @PostMapping("/insert")
    public ResponseEntity<Void> insert(AdminDTO adminDTO) {
        adminDTO.setAdminActive(true);
        return ResponseEntity.ok().build();
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
    @ResponseBody
    public List<BranchDTO> insertbranch(@RequestParam String centerName) {
        log.info("헤헤"+centerName);
        return adminService.getBranchList(centerName);
    }

    @GetMapping("/searchfacility")
    @ResponseBody
    public List<FacilityDTO> insertfacility(@RequestParam String centerName) {
        log.info("헤헤"+centerName);
        return adminService.getFacilityList(centerName);
    }

//    @GetMapping("/searchstore")
//    public List<StoreDTO> insertstore(@RequestParam String branchName, @RequestParam String facilityName) {
//        if (branchName != null) {
//            return adminService.getBranch(branchName);
//    }

}
