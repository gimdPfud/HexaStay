package com.sixthsense.hexastay.controller;

import com.sixthsense.hexastay.dto.*;
import com.sixthsense.hexastay.entity.Admin;
import com.sixthsense.hexastay.entity.Company;
import com.sixthsense.hexastay.entity.Salaries;
import com.sixthsense.hexastay.repository.AdminRepository;
import com.sixthsense.hexastay.repository.HotelRoomRepository;
import com.sixthsense.hexastay.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.List;
import java.util.Set;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/salaries")
public class SalariesController {

    private final AdminRepository adminRepository;
    private final SalariesService salariesService;


    //급여용
    @GetMapping("/list")
    public String salaries(Pageable pageable, Principal principal, Model model) {
        Admin admin = adminRepository.findByAdminEmail(principal.getName());
        if (admin == null) {
            model.addAttribute("error", "직원 정보를 찾을 수 없습니다.");
            return "/salaries/list";
        }

        Page<SalariesDTO> salariesList = salariesService.getSalariesList(admin.getAdminEmail(), pageable);
        model.addAttribute("salList", salariesList);
        return "/salaries/list";
    }

    @GetMapping("/insert")
    public String insertSalaries(Principal principal, Pageable pageable, Model model) {
        String email = principal.getName();
        Page<AdminDTO> adminList = salariesService.getSalarAdminList(email, pageable);
        model.addAttribute("adminDTOList", adminList);
        return "salaries/insert";
    }

    @PostMapping("/insert")
    public String insertPost(SalariesDTO salariesDTO){
        salariesService.postSalaries(salariesDTO);
                return "redirect:/salaries/list";
    }

}
