package com.sixthsense.hexastay.controller;


import com.sixthsense.hexastay.dto.HotelRoomDTO;
import com.sixthsense.hexastay.service.HotelRoomService;
import com.sixthsense.hexastay.service.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/admin/hotelroom")
public class HotelRoomController {

    //member 서비스 가져오기
    private final MemberService memberService;

    //호텔 룸 서비스 가져오기
    private final HotelRoomService hotelRoomService;


    //0411
    @GetMapping("/latest")
    @ResponseBody
    public ResponseEntity<HotelRoomDTO> getLatestHotelRoom(@RequestParam String roomType) {
        HotelRoomDTO hotelRoomDTO = hotelRoomService.getLatestHotelRoomByType(roomType);
        log.info(hotelRoomDTO.toString() + "dkdkdlkfjlkdsjdlfkjlkdjfdlkjlk");
        return hotelRoomDTO != null ? ResponseEntity.ok(hotelRoomDTO) : ResponseEntity.notFound().build();
    }


    //호텔룸 등록

    //호텔룸 등록페이지

    //todo:http://localhost:8090/admin/hotelroom/input
    @GetMapping("/input")
    public String inputHotelRoomGet(Model model

    )
    {
        log.info("Get 등록 페이지 진입");

        return "hotelroom/inputhotelroom";
    }

    //todo:http://localhost:8090/admin/hotelroom/input
    @PostMapping("/input")
    public String inputHotelRoomPost( HotelRoomDTO hotelRoomDTO,
                                     RedirectAttributes redirectAttributes) throws IOException
    {
        if (hotelRoomDTO == null) {
            redirectAttributes.addFlashAttribute("error", "잘못된 요청입니다.");
            return "redirect:/admin/hotelroom/input";
        }

        log.info("호텔룸 등록 요청: {}", hotelRoomDTO);
        hotelRoomService.hotelroomInsert(hotelRoomDTO);

        redirectAttributes.addFlashAttribute("message", "호텔룸이 성공적으로 등록되었습니다.");
        return "redirect:/admin/hotelroom/input"; // 등록 후 목록으로 이동
    }


















}
