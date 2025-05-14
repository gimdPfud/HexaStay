package com.sixthsense.hexastay.controller;


import com.sixthsense.hexastay.dto.AdminDTO;
import com.sixthsense.hexastay.dto.CompanyDTO;
import com.sixthsense.hexastay.repository.AdminRepository;
import com.sixthsense.hexastay.service.AdminService;
import com.sixthsense.hexastay.service.CompanyService;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
@ToString
@RequestMapping("/company")
public class CompanyController {

    private final CompanyService companyService;
    private final AdminService adminService;
    private final AdminRepository adminRepository;

    @GetMapping("/list")
    public String listCompany(@RequestParam(required = false) String choice,
                              @RequestParam(required = false) String select,
                              @RequestParam(required = false) String keyword,
                              Model model,
                              @PageableDefault(size = 10, sort = "id") Pageable pageable,
                              Principal principal) {

        String email = principal.getName();
        Long adminNum = adminService.adminFindEmail(email).getAdminNum();
        Long companyNum = adminService.adminFindEmail(email).getCompanyNum();

        if (choice == null) {
            choice = "";
        }

        if (select == null) {
            select = "전체";
        }
        
        if (keyword == null) {
            keyword = "";
        }

        // 키워드가 빈 문자열이고 검색 조건이 있는 경우 검색 조건 초기화
        if (keyword.trim().isEmpty() && !select.equals("전체")) {
            select = "전체";
        }

        Page<CompanyDTO> companyDTOS = companyService.companySearchList(select, choice, keyword, companyNum, adminNum, pageable);

        model.addAttribute("companyDTOS", companyDTOS);
        model.addAttribute("choice", choice);
        model.addAttribute("select", select);
        model.addAttribute("keyword", keyword);

        return "company/list";
    }

    @GetMapping("/insert")
    public String insertCompanyGet(Model model){

        model.addAttribute("company", companyService.companyList());

        return "company/insert";
    }

    @PostMapping("/insert")
    public String insertCompanyPost(CompanyDTO companyDTO) throws IOException {

        companyService.companyInsert(companyDTO);

        return "redirect:/company/list";
    }

    @GetMapping("/read/{companyNum}")
    public String readCompany(@PathVariable(name = "companyNum") Long companyNum, Model model) {
        CompanyDTO companyDTO = companyService.companyRead(companyNum);

        if (companyDTO == null) {
            return "redirect:/company/list";
        }
        model.addAttribute("companyDTO", companyDTO);
        return "company/read";
    }

    @GetMapping("/modify/{companyNum}")
    public String modifyCompanyGet(Model model, @PathVariable(name = "companyNum") Long companyNum) {

        CompanyDTO companyDTO = companyService.companyRead(companyNum);
        model.addAttribute("companyDTO", companyDTO);

        return "company/modify";
    }

    @PostMapping("/modify")
    public String modifyCompanyPost(CompanyDTO companyDTO) throws IOException {

        if (companyDTO.getCompanyPicture().isEmpty()) {
            if (!companyService.companyRead(companyDTO.getCompanyNum()).getCompanyPictureMeta().isEmpty())
            companyDTO.setCompanyPictureMeta(companyService.companyRead(companyDTO.getCompanyNum()).getCompanyPictureMeta());
    }
        companyService.companyModify(companyDTO);

        return "redirect:/company/list";
    }

    @PostMapping("/deactivate/{companyNum}")
    public String deactivateCompany(@PathVariable(name = "companyNum") Long companyNum) throws IOException {

        companyService.deactivateCompany(companyNum);

        return "redirect:/company/read/" + companyNum;
    }

    @PostMapping("/activate/{companyNum}")
    public String activateCompany(@PathVariable(name = "companyNum") Long companyNum) {
        companyService.activateCompany(companyNum); // 내부에서 status를 ACTIVE로 변경
        return "redirect:/company/read/" + companyNum;
    }

    @GetMapping("/{companyNum}/admins")
    @ResponseBody
    public ResponseEntity<List<AdminDTO>> getAdminsByCompany(@PathVariable Long companyNum) {
        List<AdminDTO> admins = companyService.getCompanyAdmins(companyNum);
        return ResponseEntity.ok(admins);
    }



    @ResponseBody
    @PostMapping("/list/store")
    public ResponseEntity listCompanyForStoreInsert(
                              @RequestParam(required = false) String choice,
                              @RequestParam(required = false) String select,
                              @RequestParam(required = false) String keyword,
                              @PageableDefault(size = 3) Pageable pageable, Principal principal) {

        String email = principal.getName();
        Long adminNum = adminService.adminFindEmail(email).getAdminNum();
        Long companyNum = adminService.adminFindEmail(email).getCompanyNum();

        if (choice == null || choice.trim().isEmpty()) {
            choice = "branch";
        }

        Page<CompanyDTO> companyDTOS = companyService.companySearchList(select, choice, keyword, companyNum, adminNum, pageable);

        return new ResponseEntity<>(companyDTOS, HttpStatus.OK);
    }

}
