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

    @GetMapping("/roommenu/orderpage")  //메소드명 연관
    //()안에 입력인수 등록, 출력값이 있으면 model
    public String orderList(@PageableDefault(page = 0) Pageable pageable,
                           @RequestParam(value="type", defaultValue = "") String type,
                           @RequestParam(value="keyword", defaultValue = "") String keyword,
                           String category,
                           Model model) {
        log.info("주문페이지 컨트롤러 리스트 진입");

        //서비스연동
        Page<RoomMenuDTO> roomMenuList = roomMenuService.RoomMenuList(pageable, type, keyword, category);

        //페이지정보 가공
        //Map<String, Integer> pageInfo = pagenationUtil.Pagination(listDTOS);
        Map<String, Integer> pageInfo = Pagination(roomMenuList);

        for (RoomMenuDTO roomMenuDTO : roomMenuList) {
            log.info(roomMenuDTO.getRoomMenuName());
        }

        //값전달(Model)
        model.addAttribute("list", roomMenuList);
        //조회정보전달
        model.addAttribute("type", type);
        model.addAttribute("keyword", keyword);
        model.addAttribute("category", category);
        //페이지정보전달
        model.addAllAttributes(pageInfo);

        return "/roommenu/orderpage"; //String 연관
    }

}
