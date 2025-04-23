package com.sixthsense.hexastay.controller;

import com.sixthsense.hexastay.dto.HotelRoomDTO;
import com.sixthsense.hexastay.dto.MemberDTO;

import com.sixthsense.hexastay.dto.RoomDTO;
import com.sixthsense.hexastay.service.HotelRoomService;
import com.sixthsense.hexastay.service.MemberService;
import com.sixthsense.hexastay.service.impl.RoomServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/")
public class RoomController {

    private final RoomServiceImpl roomServiceimpl;

    private final HotelRoomService hotelRoomService;

    private final MemberService memberService;


    /*키워드로 받는 멤버 검색요 메소드 */
    @GetMapping(value = "/admin/member/search", produces = "application/json")
//    @GetMapping("/admin/member/search")
    @ResponseBody
    public List<MemberDTO> searchMembers(@RequestParam("keyword") String keyword) {
        return memberService.searchByNameOrEmail(keyword);
    }

    //todo:memberByhotelRoom.html 에서 쓰이는 메소드
    @PostMapping("/admin/room/update-member")
    @ResponseBody
    public ResponseEntity<?> updateRoomMember(@RequestParam("roomNum") Long roomNum,
                                              @RequestParam("newMemberNum") Long newMemberNum) {
        try {
            roomServiceimpl.updateRoomMember(roomNum, newMemberNum);
            return ResponseEntity.ok("회원 변경 성공");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("회원 변경 실패: " + e.getMessage());
        }
    }


    // 회원을 기준으로 호텔룸 등록 페이지
    //todo:http://localhost:8090/register-hotelroom?continue
    @GetMapping("/register-hotelroom")
    public String showRegisterHotelRoomPage(Model model) {
        model.addAttribute("memberDTO", new MemberDTO());
        model.addAttribute("hotelRoomDTO", new HotelRoomDTO());
        return "room/hotelroominsert";
    }

    // 회원을 기준으로 호텔룸 등록 처리
    //memberPkRoominsert
    //todo:http://localhost:8090/register-hotelroom?continue
    @PostMapping("/register-hotelroom")
    public String registerHotelRoomForMember(@ModelAttribute MemberDTO memberDTO,
                                             @ModelAttribute HotelRoomDTO hotelRoomDTO,
                                             RedirectAttributes redirectAttributes) {
        try {
            roomServiceimpl.memberPkRoominsert(memberDTO, hotelRoomDTO);
            redirectAttributes.addFlashAttribute("successMessage", "회원 기준 호텔룸이 성공적으로 등록되었습니다.");
        } catch (Exception e) {
            log.error("회원 기준 호텔룸 등록 실패: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "회원 기준 호텔룸 등록에 실패했습니다.");
        }
        return "redirect:/register-hotelroom";
    }

    /**
     * 회원이 특정 호텔룸에 배정되는 등록 페이지 이동
     */
    //todo:http://localhost:8090/member-insertroom
    @GetMapping("/member-insertroom")
    public String insertMemberGet(Model model,@PageableDefault(page=1)Pageable pageable) {
        Page<HotelRoomDTO> hotelRoomList = hotelRoomService.hotelroomList(pageable); // 호텔룸 목록 조회
        model.addAttribute("hotelRoomList", hotelRoomList); // hotelRoomList를 모델에 추가
        model.addAttribute("memberDTO", new MemberDTO());
        return "room/memberinsertroom";
    }

    /**
     * 회원을 특정 호텔룸에 배정하고 Room 테이블에도 저장
     */
    //todo:http://localhost:8090/member-insertroom
    @PostMapping("/member-insertroom")
    public String registerMember(@ModelAttribute MemberDTO memberDTO, RedirectAttributes redirectAttributes) {
        try {
            // 호텔룸 정보 가져오기
            HotelRoomDTO hotelRoomDTO = new HotelRoomDTO();
            hotelRoomDTO.setHotelRoomNum(memberDTO.getHotelRoomNum()); // 회원이 선택한 호텔룸 번호 세팅

            log.info("회원 등록 요청 - 회원: {}, 배정 호텔룸: {}", memberDTO.getMemberName(), hotelRoomDTO.getHotelRoomNum());

            // 회원을 호텔룸에 배정하고, Room 엔티티에도 저장
            roomServiceimpl.hotelRoomPkMemberinsert(hotelRoomDTO, memberDTO);

            redirectAttributes.addFlashAttribute("message", "회원이 성공적으로 호텔룸에 배정되었습니다.");
            return "redirect:/member-insertroom"; // 성공 시 다시 등록 페이지로 이동
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
                                        @RequestParam(required = false) Long roomNum,
                                        Model model) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("roomNum").descending());
        Page<MemberDTO> members = roomServiceimpl.getMembersByHotelRoom(hotelRoomNum, pageable);

        model.addAttribute("members", members);
        model.addAttribute("currentPage", page);
        model.addAttribute("hotelRoomNum", hotelRoomNum);
        model.addAttribute("roomNum", roomNum); // ➕ Thymeleaf에서 수정 등에 사용 가능
        return "room/membersByHotelRoom";
    }

    //member가 가지고 있는 hotelroom fk 보기
    @GetMapping("/hotelRoomsByMember/{memberNum}")
    public String getHotelRoomsByMember(@PathVariable Long memberNum,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(required = false) Long roomNum, // 🔥 추가된 부분
                                        Model model) {
        log.info("🔍 호텔룸 조회 요청 - memberNum: {}, roomNum: {}", memberNum, roomNum);  // 로그에 roomNum도 추가

        Pageable pageable = PageRequest.of(page, 10, Sort.by("roomNum").descending());

        Page<HotelRoomDTO> hotelRooms = roomServiceimpl.getHotelRoomsByMember(memberNum, pageable);

        if (hotelRooms.isEmpty()) {
            log.warn("🚨 해당 회원에 대한 호텔룸 정보가 없습니다. memberNum: {}", memberNum);
        }

        model.addAttribute("hotelRooms", hotelRooms);
        model.addAttribute("currentPage", page);
        model.addAttribute("roomNum", roomNum); // 🔥 roomNum을 View에도 전달 (선택)

        return "room/hotelRoomsByMember";
    }

    //Room 인즈키 (RoomPassword 확인용)
    @GetMapping("/roomlist/roompassword")
    public String showPasswordPage() {

        return "room/password"; // 비밀번호 입력 페이지
    }

    @PostMapping("/roomlist/roompassword")
    public String checkPassword(@RequestParam("roomPassword") String roomPassword,
                                RedirectAttributes redirectAttributes) {

        if (roomServiceimpl.RoomPassword(roomPassword)) {
            return "redirect:/main"; // 메인 페이지로 이동
        } else {
            redirectAttributes.addFlashAttribute("error", "비밀번호가 일치하지 않습니다.");
            return "redirect:/roomlist/roompassword";
        }
    }



}
