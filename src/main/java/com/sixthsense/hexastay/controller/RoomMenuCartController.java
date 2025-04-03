package com.sixthsense.hexastay.controller;

import com.sixthsense.hexastay.dto.RoomMenuCartDTO;
import com.sixthsense.hexastay.service.RoomMenuCartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@Log4j2

public class RoomMenuCartController {

    private final RoomMenuCartService roomMenuCartService;

    @PostMapping("/roommenu/orderpage")
    public ResponseEntity<RoomMenuCartDTO> addToCart(@RequestParam Long memberNum,
                                                     @RequestParam Long roomMenuNum,
                                                     @RequestParam Integer amount) {
        try {
            // 서비스에서 장바구니에 상품을 추가하고 결과 DTO를 반환
            RoomMenuCartDTO roomMenuCartDTO = roomMenuCartService.insertRoomMenuCart(memberNum, roomMenuNum, amount);

            // 성공적으로 장바구니에 상품을 추가했다면 200 OK 반환
            return new ResponseEntity<>(roomMenuCartDTO, HttpStatus.OK);

        } catch (Exception e) {
            // 예외 처리: 오류 발생 시 500 Internal Server Error 반환
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
