package com.sixthsense.hexastay.controller;

import com.sixthsense.hexastay.dto.RoomCareDTO;
import com.sixthsense.hexastay.entity.HotelRoom;
import com.sixthsense.hexastay.repository.HotelRoomRepository;
import com.sixthsense.hexastay.service.RoomCareService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2

public class RoomCareController {

    private final RoomCareService roomCareService;
    private final HotelRoomRepository hotelRoomRepository;

    @GetMapping("/roomcare/insert")
    public String roomCareInsertGet(Model model) {
        log.info("룸케어 컨트롤러 진입 Get 진입");

        // hotelRoom 목록 조회
        List<HotelRoom> hotelRooms = hotelRoomRepository.findAll();
        model.addAttribute("hotelRooms", hotelRooms); // ← View에서 select 만들 때 씀
        model.addAttribute("roomCareDTO", new RoomCareDTO());

        return "roomcare/insert"; // 모바일 전용 폼 페이지
    }

    @PostMapping("/roomcare/insert")
    public String roomCareInsertPost(@ModelAttribute RoomCareDTO roomCareDTO) {
        log.info("룸케어 컨트롤러 진입 Post 진입");
        log.info("넘어온 호텔룸 번호: {}", roomCareDTO.getHotelRoomNum());

        roomCareService.RoomCareInsert(roomCareDTO);

        return "redirect:/main"; // 등록 후 감사 페이지
    }


}
