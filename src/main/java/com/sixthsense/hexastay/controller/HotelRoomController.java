package com.sixthsense.hexastay.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class HotelRoomController {

    /*리스트 페이지 컨트롤러*/
    @GetMapping("/hotelroom/list")
    public String listHotelRoom() {

        return "hotelroom/lista";
    }

    @GetMapping("/hotelroom/insert")
    public String insertGetHotelRoom() {

        return "hotelroom/insert";
    }



    @PostMapping("/hotelroom/insert")
    public String insertPostHotelRoom() {

        return "hotelroom/insert";
    }

}
