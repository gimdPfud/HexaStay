package com.sixthsense.hexastay.controller;

import com.sixthsense.hexastay.dto.RoomMenuCartDTO;
import com.sixthsense.hexastay.service.RoomMenuCartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
@Log4j2

public class RoomMenuCartController {

    private final RoomMenuCartService roomMenuCartService;

    @PostMapping("/roommenu/cart/")
    public String createRoomMenuCart(RoomMenuCartDTO roomMenuCartDTO, Model model) {
        // 장바구니 등록 서비스 호출
        RoomMenuCartDTO menuCartDTO = roomMenuCartService.roomMenuCartInsert(roomMenuCartDTO);

        // 결과 반환
        model.addAttribute("menuCartDTO", menuCartDTO);
        return "roommenu/cart";  // 적절한 뷰로 리다이렉트 또는 포워드
    }

    // 장바구니 등록 API
    @PostMapping("/roommenu/testorder")
    public ResponseEntity<RoomMenuCartDTO> addToCart(@RequestBody RoomMenuCartDTO roomMenuCartDTO) {
        RoomMenuCartDTO createdCart = roomMenuCartService.roomMenuCartInsert(roomMenuCartDTO);
        return ResponseEntity.ok(createdCart);
    }

     // 장바구니 목록 조회 API
    @GetMapping("/{memberId}")
    public ResponseEntity<RoomMenuCartDTO> getCart(@PathVariable Long memberId) {
        RoomMenuCartDTO cart = roomMenuCartService.getCartByMemberId(memberId);
        return ResponseEntity.ok(cart);
    }




}
