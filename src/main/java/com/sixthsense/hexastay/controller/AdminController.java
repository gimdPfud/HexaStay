package com.sixthsense.hexastay.controller;

import com.sixthsense.hexastay.config.Security.CustomAdminDetails;
import com.sixthsense.hexastay.dto.*;
import com.sixthsense.hexastay.entity.Admin;
import com.sixthsense.hexastay.entity.Company;
import com.sixthsense.hexastay.repository.AdminRepository;
import com.sixthsense.hexastay.repository.CompanyRepository;
import com.sixthsense.hexastay.repository.StoreRepository;
import com.sixthsense.hexastay.service.AdminService;
import com.sixthsense.hexastay.service.CompanyService;
import jakarta.validation.Valid;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.Collections;
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
    private final CompanyRepository companyRepository;
    private final StoreRepository storeRepository;
    private final PasswordEncoder passwordEncoder;


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
    public String insert(Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/admin/login";
        }
        AdminDTO adminDTO = adminService.adminFindEmail(principal.getName());
        if (adminDTO == null) {
            return "redirect:/admin/logout";
        }

        List<CompanyDTO> companyList;
        if (adminDTO.getAdminRole().equals("superAdmin")) {

            companyList = companyService.getAllList();
        } else if (adminDTO.getAdminRole().equals("EXEC") || adminDTO.getAdminRole().equals("HEAD")) {

            companyList = companyService.getCompanyAndSubsidiaries(adminDTO.getCompanyNum());
        } else {
            companyList = Collections.singletonList(companyService.companyRead(adminDTO.getCompanyNum()));
        }

        model.addAttribute("companyList", companyList);
        model.addAttribute("adminRole", adminDTO.getAdminRole());
        model.addAttribute("adminDTO", new AdminDTO());
        return "admin/insert";
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
    public String insert(@Valid @ModelAttribute("adminDTO") AdminDTO adminDTO, BindingResult bindingResult, Model model) {
        log.info("컴퍼니 들어왓나?" + adminDTO.getCompanyNum());
        if (bindingResult.hasErrors()) {
            log.info("유효성 검사 오류 발생");
            bindingResult.getAllErrors().forEach(error -> {
                log.error("유효성 검사 오류: {}", error.getDefaultMessage());
            });

            List<CompanyDTO> companyList = adminService.insertSelectCompany("center");
            model.addAttribute("companyList", companyList);
            return "admin/insert";
        }

        // 이메일 중복 체크
        if (adminRepository.findByAdminEmail(adminDTO.getAdminEmail()) != null) {
            log.info("이메일 중복 체크 실패");
            bindingResult.rejectValue("adminEmail", "duplicate", "이미 사용 중인 이메일입니다.");
            List<CompanyDTO> companyList = adminService.insertSelectCompany("center");
            model.addAttribute("companyList", companyList);
            return "admin/insert";
        }

        try {
            adminDTO.setAdminActive("INACTIVE");
            adminDTO.setAdminPassword(passwordEncoder.encode(adminDTO.getAdminPassword()));
            adminService.insertAdmin(adminDTO);
            return "redirect:/admin/list";

        } catch (IOException e) {

            log.error("회원 가입 중, 오류 발생", e);
            model.addAttribute("error", "회원 가입 중, 오류가 발생했습니다.");
            List<CompanyDTO> companyList = adminService.insertSelectCompany("center");
            model.addAttribute("companyList", companyList);
            return "admin/insert";
        }
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
        log.info("=== 회원 승인 페이지 접근 ===");
        List<AdminDTO> adminDTOList = adminService.getWaitAdminList();
        log.info("승인 대기 중인 회원 수: {}", adminDTOList.size());
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
    public String mypage(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/admin/login";
        }
        
        try {
            AdminDTO adminDTO = adminService.adminFindEmail(principal.getName());
            if (adminDTO == null) {
                log.error("로그인 정보를 찾을 수 없습니다.: {}", principal.getName());
                return "redirect:/admin/logout";
            }
            
            model.addAttribute("adminDTO", adminDTO);
            return "admin/mypage";
        } catch (Exception e) {
            log.error("마이페이지 접근 중 오류 발생: ", e);
            return "redirect:/admin/logout";
        }
    }

    @PostMapping("/mypage")
    public String mypageUpdate(@Valid @ModelAttribute("adminDTO") AdminDTO adminDTO, BindingResult bindingResult, Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/admin/login";
        }

        if (bindingResult.hasErrors()) {
            log.info("유효성 검사 오류 발생");
            return "admin/mypage";
        }

        try {
            // 현재 로그인한 사용자의 정보를 가져옴
            AdminDTO currentAdmin = adminService.adminFindEmail(principal.getName());
            if (currentAdmin == null) {
                log.error("로그인 정보를 찾을 수 없습니다.: {}", principal.getName());
                return "redirect:/admin/logout";
            }

            // 수정할 수 없는 필드들은 현재 값으로 유지
            adminDTO.setAdminNum(currentAdmin.getAdminNum());
            adminDTO.setAdminEmail(currentAdmin.getAdminEmail());
            adminDTO.setAdminRole(currentAdmin.getAdminRole());
            adminDTO.setAdminEmployeeNum(currentAdmin.getAdminEmployeeNum());
            adminDTO.setAdminResidentNum(currentAdmin.getAdminResidentNum());
            adminDTO.setCompanyNum(currentAdmin.getCompanyNum());

            adminService.adminUpdate(adminDTO);
            return "redirect:/admin/mypage";
        } catch (Exception e) {
            log.error("마이페이지 수정 중 오류 발생: ", e);
            model.addAttribute("error", "정보 수정 중 오류가 발생했습니다.");
            return "admin/mypage";
        }
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


    @GetMapping("/update/{adminNum}")
    public String adminUpdate(@PathVariable Long adminNum, Model model) throws IOException {
        AdminDTO adminDTO = adminService.adminRead(adminNum);
        model.addAttribute("adminDTO", adminDTO);
        return "admin/mypage";
    }

}
