package com.sixthsense.hexastay.controller;


import com.sixthsense.hexastay.dto.HotelRoomDTO;
import com.sixthsense.hexastay.dto.MemberDTO;
import com.sixthsense.hexastay.service.HotelRoomService;
import com.sixthsense.hexastay.service.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Log4j2
@RequestMapping("/hotelroom")
@RequiredArgsConstructor
public class HotelRoomController {

    //member 서비스 가져오기
    private final MemberService memberService;

    //호텔 룸 서비스 가져오기
    private final HotelRoomService hotelRoomService;




    /*리스트 페이지 컨트롤러*/
    @GetMapping("/list")
    public String listHotelRoom() {

        return "hotelroom/lista";
    }



    //localhost:8090/hotelroom/insert
    //호텔룸 멤버 등록 페이지
    @PostMapping("/insert")
    public String insertMembertHotelRoom(MemberDTO memberDTO) {
        log.info("호텔룸 등록 페이지에 들어 왔니:memberDTO를 가져와라 " + memberDTO);

        //view 보낸 member 등록을 저장
        memberService.memberinsert(memberDTO);



        return "hotelroom/insert";
    }



    @GetMapping("/{hotelRoomNum}")
    public String showHotelRoomDetail(@PathVariable Long hotelRoomNum, Model model) {
        hotelRoomService.getHotelRoomWithMember(hotelRoomNum).ifPresent(hotelRoomDTO -> {
            model.addAttribute("hotelRoom", hotelRoomDTO);
        });
        return "hotelRoomDetail";
    }

    @GetMapping("/members")
    public String showHotelRoomMembers(Model model) {
        List<MemberDTO> members = hotelRoomService.getAllMembersInHotelRooms();
        model.addAttribute("members", members);
        return "hotelRoomMembers"; // 해당 정보를 보여줄 HTML 페이지
    }

    // 호텔룸 등록 폼 페이지
    @GetMapping("/register")
    public String showRegisterHotelRoomForm(Model model) {
        model.addAttribute("hotelRoomDTO", new HotelRoomDTO());
        return "hotelroom/hotelroominsert";
    }

    // 호텔룸 등록 처리
    @PostMapping("/register")
    public String registerHotelRoom(@ModelAttribute HotelRoomDTO hotelRoomDTO, @RequestParam Long memberNum) {
        hotelRoomService.insertHotelRoomMember(hotelRoomDTO, memberNum);
        return "redirect:/hotelroom/register";
    }






}
