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

    /**************************************************
     * 메소드명 : roomCareInsertGet
     * 룸 케어 요청 등록 페이지 조회
     * 기능: 룸 케어 요청을 등록할 수 있는 페이지를 반환합니다. 로그인 상태에 따라 게스트 여부를 판단하고,
     * 전체 호텔 객실 목록과 빈 룸 케어 DTO를 모델에 추가하여 뷰로 전달합니다.
     * 작성자 : 김윤겸
     * 등록일 : 2025-04-24
     * 수정일 : -
     **************************************************/

    @GetMapping("/roomcare/insert")
    public String roomCareInsertGet(Model model, Principal principal) {
        log.info("룸케어 컨트롤러 진입 Get 진입 --> 필요없는 컨트롤일수도 있음.");

        boolean isGuest = principal == null;
        log.info("로그인한 사용자 : " + principal.getName());

        List<HotelRoom> hotelRooms = hotelRoomRepository.findAll();

        model.addAttribute("hotelRooms", hotelRooms);
        model.addAttribute("roomCareDTO", new RoomCareDTO());
        model.addAttribute("isGuest", isGuest);

        return "roomcare/insert";
    }

    /**************************************************
     * 메소드명 : roomCareInsertPost
     * 룸 케어 요청 처리
     * 기능: 사용자가 입력한 룸 케어 요청 정보(RoomCareDTO)를 받아 서비스 계층으로 전달하여 처리하고, 메인 페이지로 리다이렉트합니다.
     * 작성자 : 김윤겸
     * 등록일 : 2025-04-24
     * 수정일 : -
     **************************************************/

    @PostMapping("/roomcare/insert")
    public String roomCareInsertPost(@ModelAttribute RoomCareDTO roomCareDTO) {
        log.info("룸케어 컨트롤러 진입 Post 진입");
        log.info("넘어온 호텔룸 번호: {}", roomCareDTO.getHotelRoomNum());
        log.info("넘어온 요구사항 메시지: {}", roomCareDTO.getRoomCareRequestMessage()); // 로그 추가

        roomCareService.RoomCareInsert(roomCareDTO);

        return "redirect:/main"; // 등록 후 감사 페이지
    }
}
