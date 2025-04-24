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

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2

public class RoomCareController {

    private final RoomCareService roomCareService;
    private final HotelRoomRepository hotelRoomRepository;

    @GetMapping("/roomcare/insert")
    public String roomCareInsertGet(Model model, Principal principal) {
        log.info("룸케어 컨트롤러 진입 Get 진입");

        boolean isGuest = principal == null;
        log.info("로그인한 사용자 : " + principal.getName());


        // hotelRoom 목록 조회
        List<HotelRoom> hotelRooms = hotelRoomRepository.findAll();
        model.addAttribute("hotelRooms", hotelRooms);
        model.addAttribute("roomCareDTO", new RoomCareDTO());
        // 로그인 정보가 있을 경우 isGuest를 false로 설정
        model.addAttribute("isGuest", isGuest);

        return "roomcare/insert"; // 모바일 전용 폼 페이지
    }

    @PostMapping("/roomcare/insert")
    public String roomCareInsertPost(@ModelAttribute RoomCareDTO roomCareDTO) {
        log.info("룸케어 컨트롤러 진입 Post 진입");
        log.info("넘어온 호텔룸 번호: {}", roomCareDTO.getHotelRoomNum());
        log.info("넘어온 요구사항 메시지: {}", roomCareDTO.getRoomCareRequestMessage()); // 로그 추가

        roomCareService.RoomCareInsert(roomCareDTO);

        return "redirect:/main"; // 등록 후 감사 페이지
    }


}
