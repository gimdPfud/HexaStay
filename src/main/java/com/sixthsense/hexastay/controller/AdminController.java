package com.sixthsense.hexastay.controller;

import com.sixthsense.hexastay.dto.AdminDTO;
import com.sixthsense.hexastay.dto.CompanyDTO;
import com.sixthsense.hexastay.dto.StoreDTO;
import com.sixthsense.hexastay.entity.Admin;
import com.sixthsense.hexastay.repository.AdminRepository;
import com.sixthsense.hexastay.repository.CompanyRepository;
import com.sixthsense.hexastay.repository.StoreRepository;
import com.sixthsense.hexastay.service.AdminService;
import com.sixthsense.hexastay.service.CompanyService;
import com.sixthsense.hexastay.service.EmailService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
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
import java.util.*;

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
    private final HttpServletRequest request;
    private final EmailService emailService;


    // 시큐리티 체크
    @GetMapping("/check-session")
    @ResponseBody
    public String checkSession() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(authentication.getPrincipal());
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
        return "admin/login";
    }

    @PostMapping("/login")
    public String adminLoginPost() {
        return "redirect:/admin/main";
    }


    @GetMapping("/main")
    public String adminMain (Model model) {
        return "admin/main";
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

        List<CompanyDTO> companyList = new ArrayList<>();
        if (adminDTO.getAdminRole().equals("SUPERADMIN")) {
            companyList = companyService.getAllList();
        } else if (adminDTO.getAdminRole().equals("EXEC") || adminDTO.getAdminRole().equals("HEAD")) {
            companyList = companyService.getCompanyAndSubsidiaries(adminDTO.getCompanyNum());
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
    public String approve(Model model, Pageable pageable, Principal principal) {
        log.info("=== 회원 승인 페이지 접근 ===");
        
        AdminDTO adminDTO = adminService.adminFindEmail(principal.getName());
        Page<AdminDTO> adminDTOPage;
        
        if ("SUPERADMIN".equals(adminDTO.getAdminRole())) {
            // 슈퍼어드민은 모든 승인 대기 회원을 볼 수 있도록 함
            adminDTOPage = adminService.getAllWaitAdminList(pageable);
        } else {
            // 다른 관리자는 기존 로직 사용
            List<AdminDTO> adminDTOList = adminService.getWaitAdminList();
            // 페이징 대응을 위한 임시 처리
            adminDTOPage = new PageImpl<>(adminDTOList, pageable, adminDTOList.size());
        }
        
        log.info("승인 대기 중인 회원 수: {}", adminDTOPage.getTotalElements());
        model.addAttribute("adminDTOList", adminDTOPage.getContent());
        model.addAttribute("page", adminDTOPage);
        return "admin/approve";
    }

    //승인 검색 처리
    @PostMapping("/approve")
    public String approveSearch(Model model, Pageable pageable, Principal principal,
                                @RequestParam(required = false) String select,
                                @RequestParam(required = false) String choice,
                                @RequestParam(required = false) String keyword) {
        log.info("=== 회원 승인 페이지 검색 처리 ===");
        log.info("검색 조건: 소속={}, 검색필드={}, 키워드={}", select, choice, keyword);
        
        AdminDTO adminDTO = adminService.adminFindEmail(principal.getName());
        Page<AdminDTO> adminDTOPage;
        
        if ("SUPERADMIN".equals(adminDTO.getAdminRole())) {
            // 슈퍼어드민은 검색 조건에 따라 모든 승인 대기 회원을 필터링
            adminDTOPage = adminService.searchWaitAdminList(select, choice, keyword, pageable);
        } else {
            // 다른 관리자는 기존 로직 사용
            List<AdminDTO> adminDTOList = adminService.getWaitAdminList();
            // 페이징 대응을 위한 임시 처리
            adminDTOPage = new PageImpl<>(adminDTOList, pageable, adminDTOList.size());
        }
        
        model.addAttribute("adminDTOList", adminDTOPage.getContent());
        model.addAttribute("page", adminDTOPage);
        model.addAttribute("select", select);
        model.addAttribute("choice", choice);
        model.addAttribute("keyword", keyword);
        return "admin/approve";
    }

    //승인 포스트 (개별 승인 처리)
    @PostMapping("/approve/action")
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
            AdminDTO currentAdmin = adminService.adminFindEmail(principal.getName());
            if (currentAdmin == null) {
                log.error("로그인 정보를 찾을 수 없습니다.: {}", principal.getName());
                return "redirect:/admin/logout";
            }

            adminService.adminUpdate(adminDTO);
            return "redirect:/admin/list";
        } catch (Exception e) {
            log.error("마이페이지 수정 중 오류 발생: ", e);
            model.addAttribute("error", "정보 수정 중 오류가 발생했습니다.");
            return "admin/list";
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
    public String adminUpdate(@PathVariable Long adminNum, Model model, Principal principal) {
        AdminDTO adminDTO = adminService.adminRead(adminNum);
        model.addAttribute("adminDTO", adminDTO);
        
        // 현재 로그인한 사용자의 역할 확인
        AdminDTO currentAdmin = adminService.adminFindEmail(principal.getName());
        model.addAttribute("adminRole", currentAdmin.getAdminRole());
        model.addAttribute("adminEmail", currentAdmin.getAdminEmail());
        
        return "admin/mypage";
    }


    // 비번 변경

    @GetMapping("/password/update")
    public String adminPasswordReset(Principal principal) {
        return "admin/passwordupdate";
    }

    @PostMapping("/password/verify")
    @ResponseBody
    public ResponseEntity<?> verifyIdentity(
        @RequestParam String name,
        @RequestParam String employeeNum,
        @RequestParam String birth,
        Principal principal
    ) {
        try {
            if (principal == null) {
                return ResponseEntity.badRequest().body("로그인이 필요합니다.");
            }

            // 현재 로그인한 사용자 정보 조회
            Admin currentAdmin = adminRepository.findByAdminEmail(principal.getName());
            if (currentAdmin == null) {
                return ResponseEntity.badRequest().body("사용자 정보를 찾을 수 없습니다.");
            }

            // 입력한 정보와 현재 로그인한 사용자 정보 비교
            if (!currentAdmin.getAdminName().equals(name) ||
                !currentAdmin.getAdminEmployeeNum().equals(employeeNum) ||
                !currentAdmin.getAdminResidentNum().startsWith(birth)) {
                return ResponseEntity.badRequest().body("본인 확인에 실패했습니다.");
            }

            return ResponseEntity.ok("본인 확인 성공");
        } catch (Exception e) {
            log.error("본인 확인 중 오류 발생", e);
            return ResponseEntity.badRequest().body("본인 확인 중 오류가 발생했습니다.");
        }
    }

    @PostMapping("/password/update")
    @ResponseBody
    public ResponseEntity<?> updatePassword(
        @RequestParam String name,
        @RequestParam String employeeNum,
        @RequestParam String birth,
        @RequestParam String currentPassword,
        @RequestParam String newPassword,
        @RequestParam String confirmPassword,
        Principal principal
    ) {
        try {
            if (principal == null) {
                return ResponseEntity.badRequest().body("로그인이 필요합니다.");
            }

            // 현재 로그인한 사용자 정보 조회
            Admin currentAdmin = adminRepository.findByAdminEmail(principal.getName());
            if (currentAdmin == null) {
                return ResponseEntity.badRequest().body("사용자 정보를 찾을 수 없습니다.");
            }

            // 입력한 정보와 현재 로그인한 사용자 정보 비교
            if (!currentAdmin.getAdminName().equals(name) ||
                !currentAdmin.getAdminEmployeeNum().equals(employeeNum) ||
                !currentAdmin.getAdminResidentNum().startsWith(birth)) {
                return ResponseEntity.badRequest().body("본인 확인에 실패했습니다.");
            }

            // 새 비밀번호 일치 확인
            if (!newPassword.equals(confirmPassword)) {
                return ResponseEntity.badRequest().body("새 비밀번호가 일치하지 않습니다.");
            }

            // 비밀번호 변경
            adminService.updatePassword(name, employeeNum, birth, currentPassword, newPassword);
            return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("비밀번호 변경 중 오류 발생", e);
            return ResponseEntity.badRequest().body("비밀번호 변경 중 오류가 발생했습니다.");
        }
    }

    @GetMapping("mypageview")
    public String myPageView (Principal principal, Model model) {
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
            return "admin/mypageview";
        } catch (Exception e) {
            log.error("마이페이지 접근 중 오류 발생: ", e);
            return "redirect:/admin/logout";
        }
    }

    @PostMapping("/updateidentity")
    @ResponseBody
    public ResponseEntity<?> verifyIdentity(@RequestParam("adminName") String name,
                                          @RequestParam("adminEmployeeNum") String employeeNum,
                                          @RequestParam("adminResidentNum") String birth) {
        try {
            log.info("본인 확인 요청 - 이름: {}, 사번: {}, 생년월일: {}", name, employeeNum, birth);
            
            Admin admin = adminRepository.findByAdminNameAndAdminEmployeeNumAndAdminResidentNumStartingWith(
                name, employeeNum, birth);
            
            if (admin == null) {
                log.info("일치하는 정보 없음");
                return ResponseEntity.ok(Map.of("success", false, "message", "일치하는 정보가 없습니다."));
            }

            // 인증번호 생성 및 이메일 전송
            String verificationCode = String.format("%06d", (int)(Math.random() * 1000000));
            emailService.sendVerificationCode(admin.getAdminEmail(), verificationCode);
            
            // 세션에 인증번호 저장
            HttpSession session = request.getSession();
            session.setAttribute("verificationCode", verificationCode);
            session.setAttribute("adminEmail", admin.getAdminEmail());
            
            log.info("본인 확인 성공 - 이메일: {}", admin.getAdminEmail());
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            log.error("본인 확인 중 오류 발생: ", e);
            return ResponseEntity.ok(Map.of("success", false, "message", "본인 확인 중 오류가 발생했습니다."));
        }
    }

    @PostMapping("/passwordcode")
    @ResponseBody
    public ResponseEntity<?> verifyCode(@RequestParam String verificationCode) {
        try {
            HttpSession session = request.getSession();
            String savedCode = (String) session.getAttribute("verificationCode");
            
            if (savedCode == null || !savedCode.equals(verificationCode)) {
                return ResponseEntity.ok(Map.of("success", false, "message", "인증번호가 일치하지 않습니다."));
            }
            
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            log.error("인증번호 확인 중 오류 발생: ", e);
            return ResponseEntity.ok(Map.of("success", false, "message", "인증번호 확인 중 오류가 발생했습니다."));
        }
    }

    @PostMapping("/resetpassword")
    @ResponseBody
    public ResponseEntity<?> resetPassword(@RequestParam String newPassword,
                                         @RequestParam String confirmPassword) {
        try {
            if (!newPassword.equals(confirmPassword)) {
                return ResponseEntity.ok(Map.of("success", false, "message", "비밀번호가 일치하지 않습니다."));
            }
            HttpSession session = request.getSession();
            String adminEmail = (String) session.getAttribute("adminEmail");
            if (adminEmail == null) {
                return ResponseEntity.ok(Map.of("success", false, "message", "세션이 만료되었습니다."));
            }
            AdminDTO adminDTO = adminService.adminFindEmail(adminEmail);
            if (adminDTO == null) {
                return ResponseEntity.ok(Map.of("success", false, "message", "사용자를 찾을 수 없습니다."));
            }
            // 비밀번호 변경
            adminDTO.setAdminPassword(passwordEncoder.encode(newPassword));
            adminService.adminUpdate(adminDTO);

            session.removeAttribute("verificationCode");
            session.removeAttribute("adminEmail");

            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            log.error("비밀번호 변경 중 오류 발생: ", e);
            return ResponseEntity.ok(Map.of("success", false, "message", "비밀번호 변경 중 오류가 발생했습니다."));
        }
    }

}
