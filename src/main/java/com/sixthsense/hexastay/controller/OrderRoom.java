package com.sixthsense.hexastay.controller;

import com.sixthsense.hexastay.dto.OrderRoomDTO;
import com.sixthsense.hexastay.service.impl.OrderRoomImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderRoom {

    //orderRoom 서비스impl 가져 오기
    private final OrderRoomImpl orderRoom;

    @GetMapping("/insert")
    public String insertOrderGet() {

        return "hotelroom/orderroom";
    }

    @PostMapping("/insert")
    public String insertOrderPost(OrderRoomDTO orderRoomDTO) {

        orderRoom.insertOrderMemRoom(orderRoomDTO);


        return "hotelroom/orderroom";
    }

}
