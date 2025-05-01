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
import com.sixthsense.hexastay.service.ZzService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Log4j2
@RequiredArgsConstructor
@Controller
@RequestMapping("/member/store/cart")
public class StoreCartController {
    private final StorecartService storecartService;
    private final ZzService zzService;
    private final OrderstoreService orderstoreService;

    /* 5. 장바구니페이지 이동해서 보기.
           ??......get?*/
    /*1. 장바구니에 담기 (등록)*/
    @ResponseBody
    @PostMapping("/insert")
    public ResponseEntity cartInsert(StorecartitemDTO dto, Principal principal, HttpSession session){
        /*todo DTO 유효성 확인*/
        if(principal == null){return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);}
        try {
            Long hotelroomNum = zzService.sessionToHotelroomNum(session);
            int result = storecartService.addCart(dto, hotelroomNum);
            HttpStatus status = HttpStatus.BAD_REQUEST;
            return switch (result) {
                case 4 -> new ResponseEntity<>(HttpStatus.OK);
                case 3 -> new ResponseEntity<>("over",status); //log.info("아이템 수량이 99개 넘은")
                case 2 -> new ResponseEntity<>("other",status); //log.info("다른 가게의 메뉴 담은")
                case 1 -> new ResponseEntity<>("found",status); //log.info("room없음")
                default -> new ResponseEntity<>("else",status); //log.info("머임?")
            };
        } catch (Exception e) {
            log.info("알수없는오류");
            throw new RuntimeException(e);
        }
    }


    /*2. 장바구니 보기 (목록)*/
    @GetMapping("/list")
    public String cartList(Model model, Principal principal, HttpSession session){
        if(principal == null){return "sample/qrcamera";}
        Long hotelroomNum = zzService.sessionToHotelroomNum(session);
        List<StorecartitemViewDTO> list = storecartService.getCartList(hotelroomNum);
        final Long[] totalPrice = {0L};

        //옵션용 map ??
        Map<Long, List<List<String>>> optionMap = new HashMap<>();

        list.forEach(dto -> {
            if(dto.getStoremenuOptions()!=null&&!dto.getStoremenuOptions().isBlank()){

                List<String> options = Arrays.stream(dto.getStoremenuOptions().split(",")).toList();

                //옵션가격 더하기
                List<List<String>> optionList = new ArrayList<>();
                for (String option : options) {
                    List<String> optionInfo = Arrays.stream(option.split(":")).toList();
                    totalPrice[0] += Long.parseLong(optionInfo.get(2))*dto.getStoremenuCount();
                    optionList.add(optionInfo);
                }

                optionMap.put(dto.getStorecartitemNum(),optionList);

            }
            //메뉴가격 더하기
            totalPrice[0] += (long) dto.getStoremenuCount() * dto.getStoremenuPrice();
        });
        optionMap.forEach((key, value) -> value.forEach(data->log.info("Key: {}, Value: {}", key, data)));
        model.addAttribute("optionMap",optionMap);
        model.addAttribute("list",list);
        model.addAttribute("totalPrice", totalPrice[0]);
        return "mobilestore/cart/list";
    }
    /*3. 장바구니 수정 (수정)*/
    @GetMapping("/modify/{cartItemId}/{count}")
    public ResponseEntity cartModify(@PathVariable("count") int count, @PathVariable("cartItemId") Long cartItemId, Principal principal, HttpSession session){
        if(principal == null){return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);}
        Long hotelroomNum = zzService.sessionToHotelroomNum(session);
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
    public ResponseEntity cartDelete(@PathVariable("cartItemId") Long cartItemId, Principal principal, HttpSession session){
        if(principal == null){return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);}
        Long hotelroomNum = zzService.sessionToHotelroomNum(session);
        if(!storecartService.validCartItemOwner(cartItemId,hotelroomNum)){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        storecartService.deleteCartItem(cartItemId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    /*5. 장바구니 비우기*/
    @PostMapping("/clear")
    public ResponseEntity cartClear(StorecartitemDTO dto, Principal principal, HttpSession session){
        if(principal == null){return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);}
        Long hotelroomNum = zzService.sessionToHotelroomNum(session);
        try {
            storecartService.clearCartItems(hotelroomNum);
            cartInsert(dto, principal, session);//저 위에 있는 등록 맞음 ^_^ ;;
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (Exception e){
            log.info("장바구니를 비울 수 없습니다.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
