package com.sixthsense.hexastay.controller;


import com.sixthsense.hexastay.dto.HotelRoomDTO;
import com.sixthsense.hexastay.service.HotelRoomService;
import com.sixthsense.hexastay.service.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    public String inputHotelRoomPost(HotelRoomDTO hotelRoomDTO

    ) throws IOException
    {
        log.info(hotelRoomDTO + " hotelroom 이미지 등록 페이지에 들어 왓니 ");
        hotelRoomService.hotelroomInsert(hotelRoomDTO);
        return "hotelroom/inputhotelroom";
    }


















}
