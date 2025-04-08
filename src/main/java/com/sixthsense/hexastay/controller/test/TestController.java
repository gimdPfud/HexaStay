package com.sixthsense.hexastay.controller.test;

import com.sixthsense.hexastay.dto.HotelRoomDTO;
import com.sixthsense.hexastay.dto.MemberDTO;
import com.sixthsense.hexastay.dto.RoomDTO;
import com.sixthsense.hexastay.service.HotelRoomService;
import com.sixthsense.hexastay.service.MemberService;
import com.sixthsense.hexastay.service.impl.RoomServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/test")
public class TestController {



    //호텔 룸 서비스 가져오기
    private final HotelRoomService hotelRoomService;

    //
    private final RoomServiceImpl roomServiceimpl;


    //********호텔룸 등록 테스트 페이지*****//
    //Test 호텔룸 등록 - GET
    @GetMapping("/hotelinsert")
    public String hotelRoomInsertForm(Model model) {
        model.addAttribute("hotelRoomDTO", new HotelRoomDTO()); // 🔹 DTO 추가
        return "test/hotelroominsert";  // Thymeleaf 템플릿 이름
    }

    // (추가) 호텔룸 등록 처리
    @PostMapping("/hotelinsert")
    public String insertHotelRoom(@ModelAttribute HotelRoomDTO hotelRoomDTO,
                                  RedirectAttributes redirectAttributes)
    {
        log.info("호텔룸 등록 요청 - 방 이름: {}", hotelRoomDTO.getHotelRoomName());

        roomServiceimpl.insertHotelRoomAndAssignMember(hotelRoomDTO);
        redirectAttributes.addFlashAttribute("message", "호텔룸 및 배정 정보가 등록되었습니다.");
        return "redirect:/test/hotelroomlist";
    }

    // 호텔룸 목록 페이지 (member 정보 포함)
    @GetMapping("/hotelroomlist")
    public String hotelRoomList(Model model, Pageable pageable) {
        Page<HotelRoomDTO> hotelRooms = hotelRoomService.roomMemberPage(pageable);
        model.addAttribute("hotelRooms", hotelRooms);
        return "test/hotelroomlist";
    }

    // 특정 호텔룸의 Room 및 Member 정보 가져오기 (Ajax 요청 처리)
    @GetMapping("/{roomId}/roominfo")
    @ResponseBody
    public ResponseEntity<?> getRoomInfo(@PathVariable Long roomId) {
        log.info("객실 정보 요청 - Room ID: {}", roomId);
        try {
            RoomDTO roomDTO = roomServiceimpl.getRoomInfoByHotelRoomId(roomId);
            return ResponseEntity.ok(roomDTO);
        } catch (Exception e) {
            log.error("객실 정보를 가져오는 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 방 정보를 찾을 수 없습니다.");
        }
    }





    //**************참고용***********//
    //2.List
    //todo:http://localhost:8090/hotelroom/room
    @GetMapping("/hotelroom")
    public String getRooms(Model model,
                           @PageableDefault(page=1) Pageable pageable)
    {

        Page<HotelRoomDTO> roomPage
                = hotelRoomService.hotelroomList(pageable);
        model.addAttribute("roomPage", roomPage);
        return "test/hotelroom"; // hotelroom.html
    }

    @GetMapping("/hotelroomaa")
    public String getRoomsa(Model model,
                            @PageableDefault(page=1)Pageable pageable)
    {

        Page<HotelRoomDTO> roomPage
                = hotelRoomService.hotelroomList(pageable);
        model.addAttribute("roomPage", roomPage);
        return "test/hotelrooma"; // hotelrooma.html
    }


    //3.호텔룸 클릭시 멤버 정보 가져오기
    @GetMapping("/hotelroom/members/{roomNum}")
    public String getRoomMembers(@PathVariable Long roomNum, Model model) {
        HotelRoomDTO roomDTO = hotelRoomService.findRoomWithMembers(roomNum);
        model.addAttribute("room", roomDTO);
        return "hotelroom/memberinfo"; // member-info.html 렌더링
    }


    @GetMapping("/hotelroom/{hotelRoomNum}")
    public String showHotelRoomDetail(@PathVariable Long hotelRoomNum, Model model) {
        hotelRoomService.getHotelRoomWithMember(hotelRoomNum).ifPresent(hotelRoomDTO -> {
            model.addAttribute("hotelRoom", hotelRoomDTO);
        });
        return "hotelroom/hotelroomdetail";
    }

    @GetMapping("/members")
    public String showHotelRoomMembers(Model model) {
        List<MemberDTO> members = hotelRoomService.getAllMembersInHotelRooms();
        model.addAttribute("members", members);
        return "hotelRoomMembers"; // 해당 정보를 보여줄 HTML 페이지
    }




}
