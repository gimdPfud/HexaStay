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
import com.sixthsense.hexastay.service.StorecartService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;

@Log4j2
@RequiredArgsConstructor
@Controller
@RequestMapping("/member/store/cart")
public class StoreCartController {
    private final StorecartService storecartService;
//    private final ZzService zzService;
//    private final OrderstoreService orderstoreService;

    /* 5. 장바구니페이지 이동해서 보기.
           ??......get?*/
    /*1. 장바구니에 담기 (등록)*/
    @ResponseBody
    @PostMapping("/insert")
    public ResponseEntity cartInsert(@Valid StorecartitemDTO dto, BindingResult bindingResult, Principal principal, HttpSession session){
        if(principal == null){return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);}
        if(bindingResult.hasErrors()){
            log.info("유효성체크");
            bindingResult.getAllErrors().forEach(log::info);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            int result = storecartService.addCart(dto, (Long) session.getAttribute("roomNum"));
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
    public String cartList(Model model, Principal principal, HttpSession session, Locale locale) {
//        log.info("카트리스트 다국어 서비스 진입 : "+locale);
        if(principal == null){
            // 기존 로직: 비로그인 시 QR 카메라 페이지로
            return "sample/qrcamera";
        }
        Long roomNum = (Long) session.getAttribute("roomNum");
//        if (roomNum == null) {
//            model.addAttribute("errorMessage", "객실 정보가 세션에 없습니다."); // 에러 메시지 전달
//            return "error/error"; // 예시 에러 페이지
//        }
        List<StorecartitemViewDTO> list = storecartService.getCartList(roomNum);

        final Long[] totalPrice = {0L};
        Map<Long, List<List<String>>> optionMap = new HashMap<>();

        list.forEach(dto -> {
            if(dto.getStoremenuOptions()!=null && !dto.getStoremenuOptions().isBlank()){
                List<String> options = Arrays.stream(dto.getStoremenuOptions().split(",")).toList();
                List<List<String>> optionList = new ArrayList<>();
                for (String option : options) {
                    List<String> optionInfo = Arrays.stream(option.split(":")).toList();
                    try {
                        totalPrice[0] += Long.parseLong(optionInfo.get(2).trim()) * dto.getStoremenuCount(); // trim() 추가
                    } catch (NumberFormatException | IndexOutOfBoundsException e) {
                    }
                    optionList.add(optionInfo);
                }
                optionMap.put(dto.getStorecartitemNum(), optionList);
            }
            if (dto.getStoremenuCount() != null && dto.getStoremenuPrice() != null) {
                totalPrice[0] += (long) dto.getStoremenuCount() * dto.getStoremenuPrice();
            } else {
                log.warn("dto 값 오류 {}", dto);
            }
        });

        model.addAttribute("optionMap", optionMap);
        model.addAttribute("list", list);
        model.addAttribute("totalPrice", totalPrice[0]);
        model.addAttribute("currentLang", locale.getLanguage());
//        log.info("패싱될 다국어 언어 :: {}", locale.getLanguage());

        return "mobilestore/cart/list";
    }

    /*3. 장바구니 수정 (수정)*/
    @GetMapping("/modify/{cartItemId}/{count}")
    public ResponseEntity cartModify(@PathVariable("count") int count, @PathVariable("cartItemId") Long cartItemId, Principal principal, HttpSession session){
        if(principal == null){return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);}
        if(count<=0){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if(!storecartService.validCartItemOwner(cartItemId,(Long) session.getAttribute("roomNum"))){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        count = storecartService.updateCount(cartItemId,count);
        return new ResponseEntity<>(count, HttpStatus.OK);
    }
    /*4. 장바구니 삭제 (진짜삭제)*/
    @GetMapping("/delete/{cartItemId}")
    public ResponseEntity cartDelete(@PathVariable("cartItemId") Long cartItemId, Principal principal, HttpSession session){
        if(principal == null){return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);}
        if(!storecartService.validCartItemOwner(cartItemId,(Long) session.getAttribute("roomNum"))){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        storecartService.deleteCartItem(cartItemId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    /*5. 장바구니 비우기*/
    @PostMapping("/clear")
    public ResponseEntity cartClear(@Valid StorecartitemDTO dto,BindingResult bindingResult, Principal principal, HttpSession session){
        if(principal == null){return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);}
        if(bindingResult.hasErrors()){
            bindingResult.getAllErrors().forEach(log::info);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            storecartService.clearCartItems((Long) session.getAttribute("roomNum"));
            cartInsert(dto, bindingResult, principal, session);//저 위에 있는 등록 맞음 ^_^ ;;
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (Exception e){
            log.info("장바구니를 비울 수 없습니다.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
