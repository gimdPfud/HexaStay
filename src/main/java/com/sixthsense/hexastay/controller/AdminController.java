package com.sixthsense.hexastay.controller;

import com.sixthsense.hexastay.dto.*;
import com.sixthsense.hexastay.service.AdminService;
import com.sixthsense.hexastay.service.CenterService;
import com.sixthsense.hexastay.service.StoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpClient;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final CenterService centerService;


    //리스트
    @GetMapping("/list")
    public String list(Model model) {
        List<AdminDTO> adminDTOList = adminService.getAdminList();
        model.addAttribute("adminDTOList", adminDTOList);
        return "admin/list";
    }


    // 등록
    @GetMapping("/insert")
    public String insert(Model model) {
        List<CenterDTO> centerDTOList = centerService.allCenterList();
        model.addAttribute("centerDTOList", centerDTOList);
        return "admin/insert";
    }

    // 등록 포스트
    @PostMapping("/insert")
    public String insert(AdminDTO adminDTO) {
        adminDTO.setAdminActive(false);
        adminService.insertAdmin(adminDTO);
        return "redirect:/list";
    }


    //승인
    @GetMapping("/approve")
    public String approve(Model model) {
        List<AdminDTO> adminDTOList = adminService.getWaitAdminList();
        model.addAttribute("adminDTOList", adminDTOList);
        return "admin/approve";
    }
    //승인 포스트
    @PostMapping("/approve")
    public ResponseEntity<Void> approve(@RequestParam Long adminNum){
        log.info("끼끼" + adminNum);
        adminService.setAdminActive(adminNum);
        return ResponseEntity.ok().build();
    }


    //마이페이지
    @GetMapping("/mypage")
    public String mypage() {
        return "admin/mypage";
    }

    //???
    @GetMapping("/insertcompany")
    public String insertcompany() {
        return "admin/insertcompany";
    }





    // 이하 가입창에서 레스트용
    @GetMapping("/searchbranch")
    @ResponseBody
    public List<BranchDTO> insertbranch(@RequestParam String centerName) {
        log.info("헤헤"+centerName);
        return adminService.getBranchList(centerName);
    }

    @GetMapping("/searchfacility")
    @ResponseBody
    public List<FacilityDTO> insertfacility(@RequestParam String centerName) {
        log.info("후후"+centerName);
        return adminService.getFacilityList(centerName);
    }

    @GetMapping("/searchstore")
    @ResponseBody
    public List<StoreDTO> insertstore() {
        log.info("허허");
            return adminService.getStoreList();

    }


}
