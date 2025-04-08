package com.sixthsense.hexastay.controller;

import com.sixthsense.hexastay.dto.MemberDTO;
import com.sixthsense.hexastay.repository.MemberRepository;
import com.sixthsense.hexastay.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;


    @GetMapping("/login")
    public String login() {
        return "/member/login";
    }

    @PostMapping("/login")
    public String loginPost() {
        return "/member/login";
    }

    @GetMapping("/main")
    public String main(Principal principal) {
        log.info(principal.getName());
        return "/member/main";
    }

    @GetMapping("/signup")
    public String signup() {
        return "/member/signup";
    }

    @PostMapping("/signup")
    public String signupPost(MemberDTO memberDTO) {
        memberDTO.setMemberPassword(passwordEncoder.encode(memberDTO.getMemberPassword()));
        memberService.memberinsert(memberDTO);
        return "/member/signup";
    }


    //멤버 테이블 삭제 버튼
    @GetMapping("/delete")
    public String delteMember(Long memberNum) {
        log.info("member Controller에 도착 했지 :" + memberNum);

        memberService.memberDelet(memberNum);

        //다시 호텔룸 insert 창으로 보냄
        return "redirect:/hotelroom/insert";
    }




}
