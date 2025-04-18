package com.sixthsense.hexastay.controller;

import com.sixthsense.hexastay.dto.ErdDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/erd")
public class ErdController {

    @GetMapping("/list")
    public String list(Pageable pageable, Model model) {
        Page<ErdDTO> page = Page.empty(pageable);
        model.addAttribute("page", page);
        return "erd/erd";
    }

    @GetMapping("/insert")
    public String insertForm(Model model) {
        log.info("Showing insert form");
        model.addAttribute("erdDto", new ErdDTO());
        return "erd/insert";
    }

    @PostMapping("/insert")
    public String insertPost(ErdDTO erdDTO, BindingResult bindingResult, Model model) {

        return "erd/insert";
    }

    @GetMapping("/update")
    public String updateForm(@RequestParam Long id, Model model) {
        ErdDTO erdDto = new ErdDTO();
        model.addAttribute("erdDto", erdDto);
        return "erd/update";
    }


    @PostMapping("/update")
    public String updatePost(ErdDTO erdDto, BindingResult bindingResult, Model model) {
            return "erd/update";
    }

    @PostMapping("/delete")
    public String deletePost(@RequestParam Long id) {
        return "redirect:/erd/list";
    }

}
