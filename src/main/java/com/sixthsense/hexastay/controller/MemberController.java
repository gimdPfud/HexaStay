package com.sixthsense.hexastay.controller;

import com.sixthsense.hexastay.repository.MemberRepository;
import com.sixthsense.hexastay.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;




    //멤버 테이블 삭제 버튼
    @GetMapping("/delete")
    public String delteMember(Long memberNum) {
        log.info("member Controller에 도착 했지 :" + memberNum);

        memberService.memberDelet(memberNum);

        //다시 호텔룸 insert 창으로 보냄
        return "redirect:/hotelroom/insert";
    }


}
