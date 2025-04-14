package com.sixthsense.hexastay.controller;

import com.sixthsense.hexastay.config.Security.CustomAdminDetails;
import com.sixthsense.hexastay.dto.*;
import com.sixthsense.hexastay.entity.Admin;
import com.sixthsense.hexastay.service.AdminService;
import com.sixthsense.hexastay.service.CompanyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Objects;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final CompanyService companyService;


    // 시큐리티 체크
    @GetMapping("/check-session")
    @ResponseBody
    public String checkSession() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return "세션 없음 (비인증 상태)";
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails userDetails) {
            return "세션 있음 - 사용자: " + userDetails.getUsername();
        }

        return "세션 있음 - 다른 사용자 객체";
    }


    @GetMapping("/login")
    public String adminLogin () {
        return "/admin/login";
    }

    @PostMapping("/login")
    public String adminLoginPost () {
        return "/admin/login";
    }


    @GetMapping("/main")
    public String adminMain (Model model) {
        return "/admin/main";
    }


    @GetMapping("/insert")
    public String adminInsert (Model model) {

        return "/admin/insert";
    }

    @PostMapping("/insert")
    public String insert(AdminDTO adminDTO) throws IOException {
        adminDTO.setAdminActive(false);
        adminService.insertAdmin(adminDTO);
        return "redirect:/admin/list";
    }

    @GetMapping("/list")
    public String adminList (Pageable pageable, Model model) {
        model.addAttribute("adminDTOList", adminService.list(pageable));
        return "/admin/list";
    }

    @PostMapping("/list")
    public String adminListSearch(@RequestParam("select") String select,
                                  @RequestParam("choice") String choice,
                                  @RequestParam("keyword") String keyword,
                                  Pageable pageable,
                                  Model model) {


        Page<AdminDTO> adminPage = adminService.searchAdmins(select, choice, keyword, pageable);

        model.addAttribute("adminDTOList", adminPage);
        model.addAttribute("select", select);
        model.addAttribute("choice", choice);
        model.addAttribute("keyword", keyword);

        return "/admin/list";
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
    public ResponseEntity<Void> approve(@RequestParam Long adminNum) {
        adminService.setAdminActive(adminNum);
        return ResponseEntity.ok().build();
    }

    //마이페이지
    @GetMapping("/mypage")
    public String mypage(Model model,Principal principal) {
        model.addAttribute("centerDTOList", adminService.adminFindEmail(principal.getName()));
        return "admin/mypage";
    }

    @GetMapping("/read/{adminNum}")
    public String adminRead(@PathVariable Long adminNum, Model model) {
        AdminDTO adminDTO = adminService.adminRead(adminNum);
        model.addAttribute("adminDTO", adminDTO);
        return "admin/read";

    }

    // 회원 수정
    @GetMapping ("/update")
    public String adminUpdate (AdminDTO adminDTO) throws IOException {
        adminService.insertAdmin(adminDTO);
        return "admin/update";
    }

    //회원 삭제

    @DeleteMapping("/delete")
    public String adminDelete(@RequestParam Long adminNum) throws IOException {
        adminService.adminDelete(adminNum);
        return "redirect:/admin/list";
    }






}
