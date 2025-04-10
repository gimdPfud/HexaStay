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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/")
public class RoomController {

    private final RoomServiceImpl roomServiceimpl;


    // 회원을 기준으로 호텔룸 등록 페이지
    //todo:http://localhost:8090/register-hotelroom
    @GetMapping("/register-hotelroom")
    public String showRegisterHotelRoomPage(Model model) {
        model.addAttribute("memberDTO", new MemberDTO());
        model.addAttribute("hotelRoomDTO", new HotelRoomDTO());
        return "room/hotelroominsert";
    }

    // 회원을 기준으로 호텔룸 등록 처리
    @PostMapping("/register-hotelroom")
    public String registerHotelRoomForMember(@ModelAttribute MemberDTO memberDTO,
                                             @ModelAttribute HotelRoomDTO hotelRoomDTO,
                                             RedirectAttributes redirectAttributes) {
        try {
            roomServiceimpl.registerHotelRoomForMember(memberDTO, hotelRoomDTO);
            redirectAttributes.addFlashAttribute("message", "회원 기준 호텔룸이 성공적으로 등록되었습니다.");
        } catch (Exception e) {
            log.error("회원 기준 호텔룸 등록 실패: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "회원 기준 호텔룸 등록에 실패했습니다.");
        }
        return "redirect:/roomlist";
    }

    /**
     * 회원이 특정 호텔룸에 배정되는 등록 페이지 이동
     */
    @GetMapping("/member-insertroom")
    public String insertMemberGet(Model model) {
        model.addAttribute("memberDTO", new MemberDTO());
        return "room/memberinsertroom";
    }

    /**
     * 회원을 특정 호텔룸에 배정하고 Room 테이블에도 저장
     */
    @PostMapping("/member-insertroom")
    public String registerMember(@ModelAttribute MemberDTO memberDTO, RedirectAttributes redirectAttributes) {
        try {
            // 호텔룸 정보 가져오기
            HotelRoomDTO hotelRoomDTO = new HotelRoomDTO();
            hotelRoomDTO.setHotelRoomNum(memberDTO.getHotelRoomNum()); // 회원이 선택한 호텔룸 번호 세팅

            log.info("회원 등록 요청 - 회원: {}, 배정 호텔룸: {}", memberDTO.getMemberName(), hotelRoomDTO.getHotelRoomNum());

            // 회원을 호텔룸에 배정하고, Room 엔티티에도 저장
            roomServiceimpl.registerMemberForHotelRoom(hotelRoomDTO, memberDTO);

            redirectAttributes.addFlashAttribute("message", "회원이 성공적으로 호텔룸에 배정되었습니다.");
            return "redirect:/roomlist"; // 성공 시 다시 등록 페이지로 이동
        } catch (Exception e) {
            log.error("회원 등록 실패: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "회원 등록 실패");
            return "redirect:/member-insertroom";
        }
    }
/******************Room 등록 페이지 종료*********************/


    //전체 페이지 보여 주는 로직
    //todo:http://localhost:8090/roomlist
    @GetMapping("/roomlist")
    public String getRoomList(@RequestParam(defaultValue = "0") int page, Model model) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("roomNum").descending());
        Page<RoomDTO> rooms = roomServiceimpl.getRooms(pageable);

        model.addAttribute("rooms", rooms);
        model.addAttribute("currentPage", page);
        return "room/roomList";
    }


    //호텔룸이 가지고 있는 member에 fk 보기
    @GetMapping("/membersByHotelRoom/{hotelRoomNum}")
    public String getMembersByHotelRoom(@PathVariable Long hotelRoomNum,
                                        @RequestParam(defaultValue = "0") int page,
                                        Model model) {
        Pageable pageable = PageRequest.of(page, 10,Sort.by("roomNum").descending());
        Page<MemberDTO> members =
                roomServiceimpl.getMembersByHotelRoom(hotelRoomNum, pageable);

        model.addAttribute("members", members);
        model.addAttribute("currentPage", page);
        model.addAttribute("hotelRoomNum", hotelRoomNum); // 🔥 Thymeleaf에서 사용하려면 꼭 필요!

        return "room/membersByHotelRoom";
    }

    //member가 가지고 있는 hotelroom fk 보기
    @GetMapping("/hotelRoomsByMember/{memberNum}")
    public String getHotelRoomsByMember(@PathVariable Long memberNum,
                                        @RequestParam(defaultValue = "0") int page,
                                        Model model) {
        log.info("🔍 호텔룸 조회 요청 - memberNum: {}", memberNum);  // 로그 추가

        Pageable pageable =
                PageRequest.of(page, 10,Sort.by("roomNum").descending());

        Page<HotelRoomDTO> hotelRooms =
                roomServiceimpl.getHotelRoomsByMember(memberNum, pageable);

        if (hotelRooms.isEmpty()) {
            log.warn("🚨 해당 회원에 대한 호텔룸 정보가 없습니다. memberNum: {}", memberNum);
        }

        model.addAttribute("hotelRooms", hotelRooms);
        model.addAttribute("currentPage", page);
        return "room/hotelRoomsByMember";  // 🔹 Thymeleaf 파일 존재 여부 확인!
    }
}
