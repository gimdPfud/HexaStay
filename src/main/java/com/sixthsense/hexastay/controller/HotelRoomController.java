package com.sixthsense.hexastay.controller;


import com.sixthsense.hexastay.dto.MemberDTO;
import com.sixthsense.hexastay.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Log4j2
@RequestMapping("/hotelroom")
@RequiredArgsConstructor
public class HotelRoomController {

    //member 서비스 가져오기
    private final MemberService memberService;

    /*리스트 페이지 컨트롤러*/
    @GetMapping("/list")
    public String listHotelRoom() {

        return "hotelroom/lista";
    }

    @GetMapping("/insert")
    public String insertGetHotelRoom(@PageableDefault(page = 1)Pageable pageable,
                                     Model model

    )
    {
        //룸배정 회원 정보 보여 주기
        Page<MemberDTO> memberDTOS =
                memberService.memberList(pageable);

        //view 페이지에 보여주기
        model.addAttribute("membersDTOs", memberDTOS);


        return "hotelroom/insert";
    }


    //멤버 등록
    @PostMapping("/insert")
    public String insertMembertHotelRoom(MemberDTO memberDTO) {
        log.info("호텔룸 등록 페이지에 들어 왔니:memberDTO를 가져와라 " + memberDTO);

        //view 보낸 member 등록을 저장
        memberService.memberinsert(memberDTO);


        return "hotelroom/insert";
    }

}
