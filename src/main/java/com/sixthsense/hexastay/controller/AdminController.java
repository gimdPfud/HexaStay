package com.sixthsense.hexastay.controller;

import com.sixthsense.hexastay.config.Security.CustomAdminDetails;
import com.sixthsense.hexastay.dto.*;
import com.sixthsense.hexastay.entity.Admin;
import com.sixthsense.hexastay.entity.Company;
import com.sixthsense.hexastay.repository.AdminRepository;
import com.sixthsense.hexastay.repository.CompanyRepository;
import com.sixthsense.hexastay.service.AdminService;
import com.sixthsense.hexastay.service.CompanyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
import java.util.NoSuchElementException;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final CompanyService companyService;
    private final AdminRepository adminRepository;


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
        String companyType = "center";
        model.addAttribute("companyList", adminService.insertSelectCompany(companyType));
        return "/admin/insert";
    }

    @ResponseBody
    @GetMapping("/insertselect")
    public List<CompanyDTO> adminInsertSearch (Model model,
                                            @RequestParam("centerNum") Long centerNum,
                                            @RequestParam("adminChoice") String adminChoice) {
        return adminService.insertSelectList(centerNum, adminChoice);
    }

    @ResponseBody
    @GetMapping("/insertstore")
    public List<StoreDTO> adminInsertStore (Model model,
                                           @RequestParam("branchFacilityNum") Long branchFacilityNum) {
        return adminService.insertStoreList(branchFacilityNum);
    }

    @PostMapping("/insert")
    public String insert(AdminDTO adminDTO) throws IOException {
        adminDTO.setAdminActive("PENDING");
        adminService.insertAdmin(adminDTO);
        return "redirect:/admin/list";
    }



    // 리스트
    @GetMapping("/list")
    public String list(Model model, Principal principal, Pageable pageable) {
        if (principal == null) {
            return "redirect:/admin/login";
        }
        
        try {
            AdminDTO adminDTO = adminService.adminFindEmail(principal.getName());
            if (adminDTO == null) {
                log.error("로그인 정보를 찾을 수 없습니다.: {}", principal.getName());
                return "redirect:/admin/logout";
            }

            Page<AdminDTO> list = adminService.listAdmin(principal.getName(), pageable);
            model.addAttribute("list", list);
            return "admin/list";
        } catch (Exception e) {
            log.error("Admin list error: ", e);
            model.addAttribute("error", "목록 조회 중 오류가 발생했습니다.");
            return "admin/list";
        }
    }

    @PostMapping("/list")
    public String list(Model model, Principal principal, Pageable pageable,
                      @RequestParam(required = false) String type,
                      @RequestParam(required = false) String keyword) {
        if (principal == null) {
            return "redirect:/admin/login";
        }
        
        try {
            AdminDTO adminDTO = adminService.adminFindEmail(principal.getName());
            if (adminDTO == null) {
                log.error("Admin not found for email: {}", principal.getName());
                return "redirect:/admin/logout";
            }

            Page<AdminDTO> list;
            if (type != null && keyword != null && !type.isEmpty() && !keyword.isEmpty()) {
                list = adminService.listAdminSearch(principal.getName(), type, keyword, pageable);
            } else {
                list = adminService.listAdmin(principal.getName(), pageable);
            }

            model.addAttribute("list", list);
            model.addAttribute("type", type);
            model.addAttribute("keyword", keyword);
            return "admin/list";
        } catch (NoSuchElementException e) {
            log.error("Admin not found: ", e);
            model.addAttribute("error", "관리자 정보를 찾을 수 없습니다.");
            return "admin/list";
        } catch (IllegalStateException e) {
            log.error("Invalid admin state: ", e);
            model.addAttribute("error", "잘못된 관리자 상태입니다.");
            return "admin/list";
        } catch (Exception e) {
            log.error("Admin list search error: ", e);
            model.addAttribute("error", "검색 중 오류가 발생했습니다.");
            return "admin/list";
        }
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

    @PostMapping("/delete")
    public String adminDelete(@RequestParam Long adminNum) throws IOException {
        adminService.adminDelete(adminNum);
        return "redirect:/admin/list";
    }






}
