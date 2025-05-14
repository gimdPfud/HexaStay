package com.sixthsense.hexastay.controller;

import com.sixthsense.hexastay.dto.AdminDTO;
import com.sixthsense.hexastay.dto.ErdDTO;
import com.sixthsense.hexastay.service.AdminService;
import com.sixthsense.hexastay.service.ErdService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/erd")
public class ErdController {

    private final ErdService erdService;
    private final AdminService adminService;

    @GetMapping("/list")
    public String list(Pageable pageable, Model model, Principal principal) {
        AdminDTO adminDTO = adminService.adminFindEmail(principal.getName());

        Page<ErdDTO> erdList = erdService.getErdList(adminDTO, pageable);
        model.addAttribute("erd", erdList);
        return "erd/list";
    }

    @GetMapping("/insert")
    public String insertForm() {
        return "erd/insert";
    }

    @PostMapping("/insert")
    public String insertPost(ErdDTO erdDTO, Principal principal) throws IOException {
        String email = principal.getName();
        AdminDTO adminDTO = adminService.adminFindEmail(email);

        if (adminDTO.getCompanyNum() != null) {
            erdDTO.setCompanyNum(adminDTO.getCompanyNum());
        } else if (adminDTO.getStoreNum() != null) {
            erdDTO.setStoreNum(adminDTO.getStoreNum());
        }

        erdService.insert(erdDTO);
        return "redirect:/erd/list";
    }

    @GetMapping("/update/{erdNum}")
    public String updateForm(@PathVariable("erdNum")Long erdNum, Model model) {
        model.addAttribute("erd", erdService.getErd(erdNum));
        return "erd/update";
    }


    @PostMapping("/update/{erdNum}")
    public String updatePost(ErdDTO erdDto) throws IOException {
        erdService.insert(erdDto);
            return "erd/update";
    }

    @PostMapping("/delete")
    public String deletePost(@RequestParam Long id) {
        return "redirect:/erd/list";
    }

}
