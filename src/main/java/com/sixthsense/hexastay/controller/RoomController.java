package com.sixthsense.hexastay.controller;

import com.sixthsense.hexastay.dto.HotelRoomDTO;
import com.sixthsense.hexastay.dto.MemberDTO;

import com.sixthsense.hexastay.dto.RoomDTO;
import com.sixthsense.hexastay.service.impl.RoomServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/")
public class RoomController {

    private final RoomServiceImpl roomServiceimpl;


    @GetMapping("/list")
    public String getRoomList(@RequestParam(defaultValue = "0") int page, Model model) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<RoomDTO> rooms = roomServiceimpl.getRooms(pageable);

        model.addAttribute("rooms", rooms);
        model.addAttribute("currentPage", page);
        return "room/roomList";
    }



    @GetMapping("/membersByHotelRoom/{hotelRoomNum}")
    public String getMembersByHotelRoom(@PathVariable Long hotelRoomNum,
                                        @RequestParam(defaultValue = "0") int page,
                                        Model model) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<MemberDTO> members = roomServiceimpl.getMembersByHotelRoom(hotelRoomNum, pageable);

        model.addAttribute("members", members);
        model.addAttribute("currentPage", page);
        return "room/membersByHotelRoom";
    }

    @GetMapping("/hotelRoomsByMember/{memberNum}")
    public String getHotelRoomsByMember(@PathVariable Long memberNum,
                                        @RequestParam(defaultValue = "0") int page,
                                        Model model) {
        log.info("🔍 호텔룸 조회 요청 - memberNum: {}", memberNum);  // 로그 추가

        Pageable pageable = PageRequest.of(page, 10);
        Page<HotelRoomDTO> hotelRooms = roomServiceimpl.getHotelRoomsByMember(memberNum, pageable);

        if (hotelRooms.isEmpty()) {
            log.warn("🚨 해당 회원에 대한 호텔룸 정보가 없습니다. memberNum: {}", memberNum);
        }

        model.addAttribute("hotelRooms", hotelRooms);
        model.addAttribute("currentPage", page);
        return "room/hotelRoomsByMember";  // 🔹 Thymeleaf 파일 존재 여부 확인!
    }
}
