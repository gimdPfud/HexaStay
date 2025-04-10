package com.sixthsense.hexastay.controller.test;

import com.sixthsense.hexastay.dto.HotelRoomDTO;
import com.sixthsense.hexastay.dto.MemberDTO;
import com.sixthsense.hexastay.dto.RoomDTO;
import com.sixthsense.hexastay.service.HotelRoomService;
import com.sixthsense.hexastay.service.MemberService;
import com.sixthsense.hexastay.service.impl.RoomServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;


@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/test")
public class TestController {

    private final MemberService memberService;


    //리스트 페이지 보여주기
    //todo:localhost:8090/test/list
    @GetMapping("/lista")
    public String listMember(){

        return "test/lista";
    }



    //todo:localhost:8090/test/input
    /*리스트 페이지 컨트롤러 Member 리스트 페이지*/
    @GetMapping("/insert")
    public String inputMemberGet(Model model,
                                 @PageableDefault(page=1) Pageable pageable

    ) {
        log.info("Getmapping 에 진입 했니 ");

        /*if (page < 0) {
            page = 0;  // 음수 방지
        }
        Pageable pageable = PageRequest.of(page, size);*/

        Page<MemberDTO> memberDTOS =
                memberService.memberList(pageable);

        model.addAttribute("memberDTOS", memberDTOS);

        return "test/insert";
    }


    //todo:localhost:8090/member/input
    //호텔룸 멤버 등록 페이지
    @PostMapping("/insert")
    public String inputMemberGetPost(MemberDTO memberDTO,
                                     Model model)
    {
        log.info("호텔룸 등록 페이지에 들어 왔니:memberDTO를 가져와라 " + memberDTO);

        //view 보낸 member 등록을 저장
        memberService.memberinsert(memberDTO);

        //HotelRoom DB 에 값을 넣어 주는 메서드
        model.addAttribute("hotelRoomDTO", new HotelRoomDTO());

        return "test/insert";
    }





}
