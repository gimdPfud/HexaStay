package com.sixthsense.hexastay.controller;

import com.sixthsense.hexastay.dto.BoardDTO;
import com.sixthsense.hexastay.dto.CenterDTO;
import com.sixthsense.hexastay.service.CenterService;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@Log4j2
@ToString
@RequestMapping("/center")
public class CenterController {

    private final CenterService centerService;

    @GetMapping("/list")
    public String listCenter(Model model, Pageable pageable){
        log.info("get 방식 center 목록 controller 진입");

        Page<CenterDTO> centerDTOS = centerService.centerList(pageable);

        model.addAttribute("centerDTOS", centerDTOS);


        return "center/list";
    }

    @GetMapping("/signup")
    public String signUpCenterGet(){
        log.info("get 방식 center 등록 controller 진입");

        return "center/signup";
    }

    @PostMapping("/signup")
    public String signUpCenterPost(CenterDTO centerDTO){
        log.info("post 방식 center 등록 controller 진입");

        centerService.centerInsert(centerDTO);

        return "redirect:/center/list";


    }



}
