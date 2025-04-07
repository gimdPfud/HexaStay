package com.sixthsense.hexastay.controller;

import com.sixthsense.hexastay.dto.RoomMenuCartDTO;
import com.sixthsense.hexastay.dto.RoomMenuDTO;
import com.sixthsense.hexastay.entity.RoomMenu;
import com.sixthsense.hexastay.service.RoomMenuCartService;
import com.sixthsense.hexastay.service.RoomMenuService;
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

import java.util.List;
import java.util.Map;

import static com.sixthsense.hexastay.util.PaginationUtil.Pagination;

@Controller
@RequiredArgsConstructor
@Log4j2

public class RoomMenuCartController {

    private final RoomMenuCartService roomMenuCartService;
    private final RoomMenuService roomMenuService;

//    @GetMapping("/roommenu/orderpage")
//    public ResponseEntity<RoomMenuCartDTO> addToCart(@RequestParam Long memberNum,
//                                                     @RequestParam Long roomMenuNum,
//                                                     @RequestParam Integer amount) {
//        try {
//            // 서비스에서 장바구니에 상품을 추가하고 결과 DTO를 반환
//            RoomMenuCartDTO roomMenuCartDTO = roomMenuCartService.insertRoomMenuCart(memberNum, roomMenuNum, amount);
//
//            // 성공적으로 장바구니에 상품을 추가했다면 200 OK 반환
//            return new ResponseEntity<>(roomMenuCartDTO, HttpStatus.OK);
//
//        } catch (Exception e) {
//            // 예외 처리: 오류 발생 시 500 Internal Server Error 반환
//            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    @GetMapping("/roommenu/orderread")
    public String orderread(){

        return "roommenu/orderread";
    }

    @GetMapping("/roommenu/orderpage")
    public String orderList(@PageableDefault(page = 0) Pageable pageable,
                            @RequestParam(value = "type", defaultValue = "") String type,
                            @RequestParam(value = "keyword", defaultValue = "") String keyword,
                            @RequestParam(value = "category", defaultValue = "") String category,
                            Model model) {
        log.info("주문페이지 컨트롤러 리스트 진입");
        log.info("type: {}", type);
        log.info("keyword: {}", keyword);
        log.info("category: {}", category);

        // 서비스 연동: 전달된 파라미터로 메뉴 리스트 필터링
        Page<RoomMenuDTO> roomMenuList = roomMenuCartService.RoomMenuList(pageable, type, keyword, category);

        // 페이지 정보 가공
        Map<String, Integer> pageInfo = Pagination(roomMenuList);

        // 로깅: 메뉴 이름 출력
        for (RoomMenuDTO roomMenuDTO : roomMenuList) {
            log.info("이것은 룸카드컨트롤러" + roomMenuDTO.getRoomMenuName());
        }

        // 값 전달 (Model)
        model.addAttribute("list", roomMenuList);
        model.addAttribute("type", type);
        model.addAttribute("keyword", keyword);
        model.addAttribute("category", category);  // 카테고리 필터링 값 전달
        model.addAllAttributes(pageInfo);

        return "/roommenu/orderpage";  // orderpage를 반환하여 뷰를 렌더링
    }

}
