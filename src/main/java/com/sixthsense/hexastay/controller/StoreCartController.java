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
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@Log4j2
@RequiredArgsConstructor
@Controller
@RequestMapping("/member/store/cart")
public class StoreCartController {
    private final StorecartService storecartService;
    private final OrderstoreService orderstoreService;

    /* 5. 장바구니페이지 이동해서 보기.
           ??......get?*/
    /*1. 장바구니에 담기 (등록)*/
    @GetMapping("/insert")
    public ResponseEntity cartInsert(StorecartitemDTO dto, Principal principal){
        /*todo DTO 유효성 확인*/
        if(principal==null){
            log.info("로그인안됨");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String email = principal.getName();
        Long cartItemId = null;
        try {
            cartItemId = storecartService.addCart(dto, email);
            log.info("카트 담김");
            return new ResponseEntity<>(cartItemId, HttpStatus.OK);
        }catch (EntityNotFoundException e){
            log.info("카트저장불가능:메뉴못찾음");
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.info("알수없는오류");
            throw new RuntimeException(e);
        }
    }
    /*2. 장바구니 보기 (목록)*/
    @GetMapping("/list")
    public String cartList(Principal principal, Model model){
        if(principal==null){
//            return "redirect:/member/login";
            /*로그인안되면.........*/
            return null;
        }
        List<StorecartitemViewDTO> list = storecartService.getCartList(principal.getName());
        model.addAttribute("list",list);
        return "mobilestore/cart/list";
    }
    /*3. 장바구니 수정 (수정)*/
    @GetMapping("/modify/{cartItemId}/{count}")
    public ResponseEntity cartModify(@PathVariable("count") int count, Principal principal, @PathVariable("cartItemId") Long cartItemId){
        if(count<=0){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if(principal==null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if(!storecartService.validCartItemOwner(cartItemId,principal.getName())){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        storecartService.updateCount(cartItemId,count);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    /*4. 장바구니 삭제 (진짜삭제)*/
    @GetMapping("/delete/{cartItemId}")
    public ResponseEntity cartDelete(@PathVariable("cartItemId") Long cartItemId, Principal principal){
        if(principal==null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if(!storecartService.validCartItemOwner(cartItemId,principal.getName())){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        storecartService.deleteCartItem(cartItemId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
