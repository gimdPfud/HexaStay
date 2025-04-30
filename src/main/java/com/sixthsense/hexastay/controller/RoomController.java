package com.sixthsense.hexastay.controller;

import com.sixthsense.hexastay.dto.HotelRoomDTO;
import com.sixthsense.hexastay.dto.MemberDTO;

import com.sixthsense.hexastay.dto.RoomDTO;
import com.sixthsense.hexastay.entity.Room;
import com.sixthsense.hexastay.service.HotelRoomService;
import com.sixthsense.hexastay.service.MemberService;
import com.sixthsense.hexastay.service.impl.RoomServiceImpl;
import jakarta.persistence.EntityNotFoundException;
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
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/")
public class RoomController {

    private final RoomServiceImpl roomServiceimpl;

    private final HotelRoomService hotelRoomService;

    private final MemberService memberService;

    //todo:0425 카테고리 분류별 페이지 리스트 localhost:8090/roomList
    //checkIN  checkOut 상태별로 보여주는 Roomlist 페이지
    @GetMapping("/roomlist/{status}")
    public String getRoomListByStatus(@PathVariable("status") String status,
                                      @PageableDefault(size = 10, sort = "roomNum", direction = Sort.Direction.DESC) Pageable pageable,
                                      Model model) {
        if (!status.equals("checkin") && !status.equals("checkout")) {
            throw new IllegalArgumentException("잘못된 상태입니다.");
        }

        Page<RoomDTO> rooms = roomServiceimpl.findRoomsByHotelRoomStatus(status, pageable);
        model.addAttribute("rooms", rooms);  // ✅ Page 객체 전달
        model.addAttribute("currentStatus", status);
        return "room/roomlist"; // ✅ 파일명은 소문자 유지
    }

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


    /*키워드로 받는 멤버 검색용 메소드 */
    @GetMapping(value = "/admin/member/search", produces = "application/json")
//    @GetMapping("/admin/member/search")
    @ResponseBody
    public List<MemberDTO> searchMembers(@RequestParam("keyword") String keyword) {
        return memberService.searchByNameOrEmail(keyword);
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
            redirectAttributes.addFlashAttribute("successMessage", "성공적으로 배정되었습니다.");
        } catch (Exception e) {
            log.error("회원 기준 객실 등록 실패: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "객실 등록에 실패했습니다.");
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

            log.info("회원 등록 요청 - 회원: {}, 배정 객실: {}", memberDTO.getMemberName(), hotelRoomDTO.getHotelRoomNum());

            // 회원을 호텔룸에 배정하고, Room 엔티티에도 저장
            roomServiceimpl.hotelRoomPkMemberinsert(hotelRoomDTO, memberDTO);

            redirectAttributes.addFlashAttribute("message", "회원이 성공적으로 객실에 배정되었습니다.");
            return "redirect:/member-insertroom"; // 성공 시 다시 등록 페이지로 이동
        } catch (Exception e) {
            log.error("회원 등록 실패: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "회원 등록 실패");
            return "redirect:/member-insertroom";
        }
    }
    /******************Room 등록 페이지 종료*********************/



    //호텔룸이 가지고 있는 member에 fk 보기
    @GetMapping("/membersByHotelRoom/{hotelRoomNum}")
    public String getMembersByHotelRoom(@PathVariable Long hotelRoomNum,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(required = false) Long roomNum,
                                        Model model) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("roomNum").descending());

        //todo:추후에 추가적으로 여기 로직을 room DB 테이블에 값을 가져 오는 메소드로 변경 가능해서 사용
        Page<MemberDTO> members = roomServiceimpl.getMembersByHotelRoom(hotelRoomNum, pageable);

        model.addAttribute("members", members);
        model.addAttribute("currentPage", page);
        model.addAttribute("hotelRoomNum", hotelRoomNum);
        model.addAttribute("roomNum", roomNum); // ➕ Thymeleaf에서 수정 등에 사용 가능
        return "room/membersByHotelRoom";
    }

    /******** Room Pk 찾아와서 : Member FK 만수정 하는 로직 ************/

    //todo:memberByhotelRoom.html 에서 쓰이는 메소드
    @PostMapping("/admin/room/update-member")
    @ResponseBody
    public String updateRoomMember(@RequestParam("roomNum") Long roomNum,
                                   @RequestParam("memberNum") Long memberNum) {
        try {
            roomServiceimpl.updateRoomMember(roomNum, memberNum);
            return "회원 정보가 성공적으로 수정되었습니다.";
        } catch (IllegalArgumentException e) {
            log.error("회원 수정 오류: {}", e.getMessage());
            return "회원 정보 수정에 실패했습니다: " + e.getMessage();
        }
    }
    /******** Room Pk 찾아와서 : Member FK 만수정 하는 로직 ************/

    //member가 가지고 있는 hotelroom fk 보기
    @GetMapping("/hotelRoomsByMember/{memberNum}")
    public String getHotelRoomsByMember(@PathVariable Long memberNum,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(required = false) Long roomNum, // 🔥 추가된 부분
                                        Model model) {
        log.info("🔍 객실 조회 요청 - memberNum: {}, roomNum: {}", memberNum, roomNum);  // 로그에 roomNum도 추가

        Pageable pageable = PageRequest.of(page, 10, Sort.by("roomNum").descending());

        Page<HotelRoomDTO> hotelRooms = roomServiceimpl.getHotelRoomsByMember(memberNum, pageable);

        if (hotelRooms.isEmpty()) {
            log.warn("🚨 해당 회원에 대한 객실 정보가 없습니다. memberNum: {}", memberNum);
        }

        model.addAttribute("hotelRooms", hotelRooms);
        model.addAttribute("currentPage", page);
        model.addAttribute("roomNum", roomNum); // 🔥 roomNum을 View에도 전달 (선택)

        return "room/hotelRoomsByMember";
    }

    //호텔룸 검색 Keyword 로 검색하는 로직
    //todo:hotelRoomsByMember.html
    @GetMapping("/admin/hotelroom/search")
    @ResponseBody
    public ResponseEntity<List<HotelRoomDTO>> searchHotelRoomsJson(@RequestParam String keyword,
                                                                   @RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<HotelRoomDTO> hotelRoomPage = hotelRoomService.searchHotelRoomsByName(keyword, pageable);
        return ResponseEntity.ok(hotelRoomPage.getContent());
    }

    /******** Room PK 찾아와서 : HotelRoom Fk 만 수정하는 로직*********/
    @PostMapping("/admin/room/update-hotelroom")
    @ResponseBody
    public String updateHotelRoomInRoom(@RequestParam Long roomNum,
                                        @RequestParam Long hotelRoomNum) {

        log.info(roomNum + "수정 컨트롤러 진입" + hotelRoomNum + "hotelRoom Num 파라미터 들어 와라");
        try {
            roomServiceimpl.updateHotelRoomInRoom(roomNum, hotelRoomNum);
            return "객실 배정이 성공적으로 수정되었습니다.";
        } catch (Exception e) {
            return "수정 중 오류 발생: " + e.getMessage();
        }
    }

    /******** Room PK 찾아와서 : HotelRoom Fk 만 수정하는 로직*********/


    //호텔룸 검색 수정 로직
    //todo:hotelRoomsByMember.html
    @PatchMapping("/admin/room/{roomNum}/hotelroom")
    @ResponseBody
    public ResponseEntity<?> updateRoomHotelRoom(@PathVariable Long roomNum,
                                                 @RequestBody Map<String, Long> request) {
        Long hotelRoomNum = request.get("hotelRoomNum");
        roomServiceimpl.updateHotelRoomInRoom(roomNum, hotelRoomNum);
        return ResponseEntity.ok().body(Map.of("message", "수정되었습니다."));
    }


    //Room 인즈키 (RoomPassword 확인용)
    @GetMapping("/qr/{hotelRoomNum}")
    public String showPasswordPage(@PathVariable("hotelRoomNum") Long hotelRoomNum,Model model   ) {

        model.addAttribute("hotelRoomNum", hotelRoomNum);

        return "room/password"; // 비밀번호 입력 페이지
    }

    @PostMapping("/qr/{hotelRoomNum}")
    public String checkPassword(@RequestParam("roomPassword") String roomPassword,
                                @PathVariable("hotelRoomNum") Long hotelRoomNum,
                                RedirectAttributes redirectAttributes) {

        try {
            Room rooms = roomServiceimpl.readRoomByHotelRoomNum(hotelRoomNum);// 이제 rooms를 받음

            if (rooms.getRoomPassword().equals(roomPassword)) {
                return "redirect:/main?hotelRoomNum=" + hotelRoomNum;
            } else {
                redirectAttributes.addFlashAttribute("error", "비밀번호가 일치하지 않습니다.");
                return "redirect:/qr/" + hotelRoomNum;
            }
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", "호텔룸 정보를 찾을 수 없습니다.");
            return "redirect:/roomlist";
        }
    }

    //Room 테이블에서 hotelRoomNum 을 가져 오는데 컬럼에 status 에서 'checkin' 벨류가 같은것만 가져옴
//    @PostMapping("/qr/{hotelRoomNum}")
//    public String checkPasswordStatus(@RequestParam("roomPassword") String roomPassword,
//                                      @PathVariable("hotelRoomNum") Long hotelRoomNum,
//                                      RedirectAttributes redirectAttributes) {
//
//        try {
//            // 🔐 체크인 상태의 Room 중 비밀번호가 일치하는 Room만 반환
////            Room room = roomServiceimpl.authenticateRoomByHotelRoomNumAndPassword(hotelRoomNum, roomPassword);
//
//            // 🔑 인증 성공 → main 페이지로 리다이렉트
//            return "redirect:/main?hotelRoomNum=" + hotelRoomNum;
//
//        } catch (IllegalArgumentException e) {
//            // ❌ 비밀번호 틀림
//            redirectAttributes.addFlashAttribute("error", "비밀번호가 일치하지 않습니다.");
//            return "redirect:/qr/" + hotelRoomNum;
//
//        } catch (EntityNotFoundException e) {
//            // ❌ 해당 호텔룸 or 체크인 상태 room 없음
//            redirectAttributes.addFlashAttribute("error", "해당 호텔룸 또는 체크인 상태의 룸이 없습니다.");
//            return "redirect:/roomlist";
//        }
//    }

}
