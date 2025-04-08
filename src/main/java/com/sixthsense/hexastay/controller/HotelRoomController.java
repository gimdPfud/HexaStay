package com.sixthsense.hexastay.controller;


import com.sixthsense.hexastay.dto.HotelRoomDTO;
import com.sixthsense.hexastay.dto.MemberDTO;
import com.sixthsense.hexastay.service.HotelRoomService;
import com.sixthsense.hexastay.service.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Log4j2

@RequiredArgsConstructor
public class HotelRoomController {

    //member 서비스 가져오기
    private final MemberService memberService;

    //호텔 룸 서비스 가져오기
    private final HotelRoomService hotelRoomService;

    //리스트 페이지 보여주기
    //todo:localhost:8090/hotelroom/list
    @GetMapping("/hotelroom/list")
    public String listHotelRoome(){

        return "hotelroom/lista";
    }


    //todo:localhost:8090/hotelroom/insert
    /*리스트 페이지 컨트롤러 Member 리스트 페이지*/
    @GetMapping("/hotelroom/insert")
    public String insertListHotelRoom(Model model,
                                      @PageableDefault(page=1)Pageable pageable

    ) {
        log.info("Getmapping 에 진입 했니 ");

        /*if (page < 0) {
            page = 0;  // 음수 방지
        }
        Pageable pageable = PageRequest.of(page, size);*/

        Page<MemberDTO> memberDTOS =
        memberService.memberList(pageable);

        model.addAttribute("memberDTOS", memberDTOS);

        return "hotelroom/insert";
    }


    //todo:localhost:8090/hotelroom/insert
    //호텔룸 멤버 등록 페이지
    @PostMapping("/insert")
    public String insertMembertHotelRoom(MemberDTO memberDTO,
    Model model)
    {
        log.info("호텔룸 등록 페이지에 들어 왔니:memberDTO를 가져와라 " + memberDTO);

        //view 보낸 member 등록을 저장
        memberService.memberinsert(memberDTO);

        //HotelRoom DB 에 값을 넣어 주는 메서드
        model.addAttribute("hotelRoomDTO", new HotelRoomDTO());

        return "hotelroom/insert";
    }
















}
