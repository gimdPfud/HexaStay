/***********************************************
 * 클래스명 : StoreCartController
 * 기능 :
 * 작성자 :
 * 작성일 : 2025-04-09
 * 수정 : 2025-04-09
 * ***********************************************/
package com.sixthsense.hexastay.controller;

import com.sixthsense.hexastay.dto.StorecartitemDTO;
import com.sixthsense.hexastay.dto.StorecartitemViewDTO;
import com.sixthsense.hexastay.service.OrderstoreService;
import com.sixthsense.hexastay.service.StorecartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Log4j2
@RequiredArgsConstructor
@Controller
@RequestMapping("/member/store/cart")
public class StoreCartController {
    private final StorecartService storecartService;
    private final OrderstoreService orderstoreService;
    Long hotelroomNum = 9L; // todo 이거 어떻게 받아오는지 나중에 다시 고쳐야 함. 흠......세션에 저장하나??

    /* 5. 장바구니페이지 이동해서 보기.
           ??......get?*/
    /*1. 장바구니에 담기 (등록)*/
    @PostMapping("/insert")
    public ResponseEntity cartInsert(StorecartitemDTO dto){
        /*todo DTO 유효성 확인*/
        try {
            Long result = storecartService.addCart(dto, hotelroomNum);
            if(result!=null){
                log.info("카트 담김");
                return new ResponseEntity<>(HttpStatus.OK);
            }
            log.info("다른가게의 메뉴 담기 또는 Room 없음");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);//
        } catch (Exception e) {
            log.info("알수없는오류");
            throw new RuntimeException(e);
        }
    }
    /*2. 장바구니 보기 (목록)*/
    @GetMapping("/list")
    public String cartList(Model model){
        List<StorecartitemViewDTO> list = storecartService.getCartList(hotelroomNum);
        AtomicReference<Long> totalpirce = new AtomicReference<>(0L);
        list.forEach(dto -> {
            totalpirce.updateAndGet(v -> v + (long) dto.getStoremenuCount() * dto.getStoremenuPrice());
        });
//        log.info(totalpirce);
        model.addAttribute("list",list);
        model.addAttribute("totalPrice",totalpirce);
        return "mobilestore/cart/list";
    }
    /*3. 장바구니 수정 (수정)*/
    @GetMapping("/modify/{cartItemId}/{count}")
    public ResponseEntity cartModify(@PathVariable("count") int count, @PathVariable("cartItemId") Long cartItemId){
        if(count<=0){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if(!storecartService.validCartItemOwner(cartItemId,hotelroomNum)){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        count = storecartService.updateCount(cartItemId,count);
        return new ResponseEntity<>(count, HttpStatus.OK);
    }
    /*4. 장바구니 삭제 (진짜삭제)*/
    @GetMapping("/delete/{cartItemId}")
    public ResponseEntity cartDelete(@PathVariable("cartItemId") Long cartItemId){
        if(!storecartService.validCartItemOwner(cartItemId,hotelroomNum)){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        storecartService.deleteCartItem(cartItemId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    /*5. 장바구니 비우기*/
    @PostMapping("/clear")
    public ResponseEntity cartClear(StorecartitemDTO dto){
        try {
            storecartService.clearCartItems(hotelroomNum);
            cartInsert(dto);//저 위에 있는 등록 맞음 ^_^ ;;
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (Exception e){
            log.info("장바구니를 비울 수 없습니다.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
