/***********************************************
 * 클래스명 : StoreCartController
 * 기능 : principal을 사용한 컨트롤러
 * 작성자 :
 * 작성일 : 2025-04-22
 * 수정 : 2025-04-22
 * ***********************************************/
package com.sixthsense.hexastay.controller;

import com.sixthsense.hexastay.dto.HotelRoomDTO;
import com.sixthsense.hexastay.dto.StorecartitemDTO;
import com.sixthsense.hexastay.dto.StorecartitemViewDTO;
import com.sixthsense.hexastay.entity.RoomService;
import com.sixthsense.hexastay.repository.MemberRepository;
import com.sixthsense.hexastay.service.OrderstoreService;
import com.sixthsense.hexastay.service.StorecartService;
import com.sixthsense.hexastay.service.impl.RoomServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import java.util.concurrent.atomic.AtomicReference;

@Log4j2
@RequiredArgsConstructor
@Controller
@RequestMapping("/member/store/cart")
public class StoreCartController {
    private final StorecartService storecartService;
    private final OrderstoreService orderstoreService;

    /*1. 장바구니에 담기 (등록)*/
    @PostMapping("/insert")
    public ResponseEntity cartInsert(StorecartitemDTO dto, Principal principal){
        if(principal==null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Long hotelroomNum = storecartService.principalToHotelroomNum(principal);
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
    public String cartList(Model model, Principal principal){
        if(principal==null){
            return "redirect:/member/login";//todo principal이 null이라면 보낼 페이지 고민해보기
        }
        Long hotelroomNum = storecartService.principalToHotelroomNum(principal);
        List<StorecartitemViewDTO> list = storecartService.getCartList(hotelroomNum);
        AtomicReference<Long> totalpirce = new AtomicReference<>(0L);
        list.forEach(dto -> {
            totalpirce.updateAndGet(v -> v + (long) dto.getStoremenuCount() * dto.getStoremenuPrice());
        });
        log.info(totalpirce);
        model.addAttribute("list",list);
        model.addAttribute("totalPrice",totalpirce);
        return "mobilestore/cart/list";
    }
    /*3. 장바구니 수정 (수정)*/
    @GetMapping("/modify/{cartItemId}/{count}")
    public ResponseEntity cartModify(@PathVariable("count") int count, @PathVariable("cartItemId") Long cartItemId, Principal principal){
        if(principal==null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Long hotelroomNum = storecartService.principalToHotelroomNum(principal);
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
    public ResponseEntity cartDelete(@PathVariable("cartItemId") Long cartItemId, Principal principal){
        if(principal==null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Long hotelroomNum = storecartService.principalToHotelroomNum(principal);
        if(!storecartService.validCartItemOwner(cartItemId,hotelroomNum)){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        storecartService.deleteCartItem(cartItemId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    /*5. 장바구니 비우기*/
    @PostMapping("/clear")
    public ResponseEntity cartClear(StorecartitemDTO dto, Principal principal){
        if(principal==null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Long hotelroomNum = storecartService.principalToHotelroomNum(principal);
        try {
            storecartService.clearCartItems(hotelroomNum);
            cartInsert(dto, principal);//저 위에 있는 등록 맞음 ^_^ ;;
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (Exception e){
            log.info("장바구니를 비울 수 없습니다.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
