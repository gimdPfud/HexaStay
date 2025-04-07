package com.sixthsense.hexastay.controller;

import com.sixthsense.hexastay.dto.RoomMenuCartDTO;
import com.sixthsense.hexastay.dto.RoomMenuDTO;
import com.sixthsense.hexastay.service.RoomMenuCartService;
import com.sixthsense.hexastay.service.RoomMenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

import static com.sixthsense.hexastay.util.PaginationUtil.Pagination;

/**************************************************
 * 클래스명 : RoomMenuCartController
 * 기능   : 룸서비스(장바구니)와 관련된 컨트롤러
 *        - 룸서비스 메뉴 목록 조회 및 필터링 기능
 *        - 장바구니 추가 및 조회 기능
 * 작성자 : 김윤겸
 * 작성일 : 2025-04-01
 * 수정일 : 2025-04-07
 * 입출력 변수 설계 : 김윤겸
 **************************************************/

@RestController
@RequiredArgsConstructor
@RequestMapping("/roommenu")
@Log4j2

public class RoomMenuCartController {

    private final RoomMenuCartService roomMenuCartService;
    private final RoomMenuService roomMenuService;

    // 장바구니에 아이템 추가
    @PostMapping("/cart")
    public RoomMenuCartDTO addRoomMenuToCart(@RequestParam Long memberNum,
                                             @RequestParam Long roomMenuNum,
                                             @RequestParam Integer amount) {
        log.info("장바구니 담기 컨트롤러 진입");
        return roomMenuCartService.insertRoomMenuCart(memberNum, roomMenuNum, amount);
    }

    // 룸서비스 상품 리스트 검색
    @GetMapping("/cart/list")
    public Page<RoomMenuDTO> getRoomMenuList(Pageable pageable,
                                             @RequestParam String type,
                                             @RequestParam(required = false) String keyword,
                                             @RequestParam(required = false) String category) {
        log.info("장바구니 리스트 컨트롤러 진입");
        return roomMenuCartService.RoomMenuList(pageable, type, keyword, category);
    }

    // (옵션) 장바구니 조회 기능 추가
    @GetMapping("/cart/{memberNum}")
    public RoomMenuCartDTO getRoomMenuCart(@PathVariable Long memberNum) {
        return roomMenuCartService.getCartByMember(memberNum);
    }


}
