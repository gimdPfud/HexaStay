package com.sixthsense.hexastay.controller;

import com.sixthsense.hexastay.entity.Admin;
import com.sixthsense.hexastay.repository.AdminRepository;
import com.sixthsense.hexastay.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@Log4j2
public class AdminResetController {

    private final AdminService adminService;
    private final AdminRepository adminRepository;

    @GetMapping("/adminreset")
    public String adminReset() {
        return "admin/adminreset";
    }

    @PostMapping("/adminreset")
    @ResponseBody
    public ResponseEntity<?> resetAdminAccount() {
        Admin admin = adminRepository.findByAdminEmail("admin@admin");
        if (admin == null) {
            admin = new Admin();
            }
            admin.setCompany(null);
            admin.setAdminActive("ACTIVE");
            admin.setAdminAddress("00000" + "\u00A0" +"테스트 주소" + "\u00A0" + "테스트 상세주소");
            admin.setAdminEmail("admin@admin");
            admin.setAdminEmployeeNum("00000000");
            admin.setAdminName("hexa admin");
            admin.setAdminPassword("$2a$10$BGJyIkC8HC9Qe7.IMS7zPudOJaNdzzlFaLrR9bJ2KihIM418WQ9hK");
            admin.setAdminPhone("010-0000-0000");
            admin.setAdminPosition("웹 관리자");
            admin.setAdminResidentNum("0000000000000");
            admin.setAdminRole("SUPERADMIN");

            adminRepository.save(admin);

            return ResponseEntity.ok(Map.of("success", true, "message", "어드민 계정이 재설정되었습니다."));

    }

    @GetMapping("/")
    public String index () {
        return "redirect:/admin/main";
    }
}
