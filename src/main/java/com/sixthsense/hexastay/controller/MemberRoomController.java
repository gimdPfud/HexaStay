package com.sixthsense.hexastay.controller;


import com.sixthsense.hexastay.dto.HotelRoomDTO;
import com.sixthsense.hexastay.dto.MemberDTO;
import com.sixthsense.hexastay.repository.MemberRepository;
import com.sixthsense.hexastay.service.impl.RoomServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/member")

public class MemberRoomController {

    private final MemberRepository memberRepository;


    private final RoomServiceImpl roomServiceimpl;

    /**
     * 회원이 특정 호텔룸에 배정되는 등록 페이지 이동
     */
    @GetMapping("/insertroom")
    public String insertMemberGet(Model model) {
        model.addAttribute("memberDTO", new MemberDTO());
        return "member_room/memberinsertroom";
    }

    /**
     * 회원을 특정 호텔룸에 배정하고 Room 테이블에도 저장
     */
    @PostMapping("/register")
    public String registerMember(@ModelAttribute MemberDTO memberDTO, RedirectAttributes redirectAttributes) {
        try {
            // 호텔룸 정보 가져오기
            HotelRoomDTO hotelRoomDTO = new HotelRoomDTO();
            hotelRoomDTO.setHotelRoomNum(memberDTO.getHotelRoomNum()); // 회원이 선택한 호텔룸 번호 세팅

            log.info("회원 등록 요청 - 회원: {}, 배정 호텔룸: {}", memberDTO.getMemberName(), hotelRoomDTO.getHotelRoomNum());

            // 회원을 호텔룸에 배정하고, Room 엔티티에도 저장
            roomServiceimpl.registerMemberForHotelRoom(hotelRoomDTO, memberDTO);

            redirectAttributes.addFlashAttribute("message", "회원이 성공적으로 호텔룸에 배정되었습니다.");
            return "redirect:/member/insertroom"; // 성공 시 다시 등록 페이지로 이동
        } catch (Exception e) {
            log.error("회원 등록 실패: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "회원 등록 실패");
            return "redirect:/member/insertroom";
        }
    }

}
