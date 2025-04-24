/***********************************************
 * 클래스명 : CartController
 * 기능 :
 * 작성자 :
 * 작성일 : 2025-04-15
 * 수정 : 2025-04-15
 * ***********************************************/
package com.sixthsense.hexastay.controller;

import com.sixthsense.hexastay.dto.HotelRoomDTO;
import com.sixthsense.hexastay.entity.Member;
import com.sixthsense.hexastay.entity.RoomService;
import com.sixthsense.hexastay.repository.MemberRepository;
import com.sixthsense.hexastay.service.RoomMenuCartService;
import com.sixthsense.hexastay.service.StorecartService;
import com.sixthsense.hexastay.service.ZzService;
import com.sixthsense.hexastay.service.impl.RoomServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;

@RequiredArgsConstructor
@Controller
@RequestMapping("/cart")
public class CartController {
    private final ZzService zzService;
    private final StorecartService storecartService;
    private final RoomMenuCartService roomMenuCartService;
    private final MemberRepository memberRepository;
    private final RoomServiceImpl roomService;
    Long hotelroomNum = 9L; //todo 나중에 qr링크에서 받기

    @GetMapping("/gocart")
    public String gocart(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        System.out.println("이전 페이지: " + referer);
        if (referer != null) {
            if (referer.contains("/roommenu")) {
                return "redirect:/roommenu/cartlist";
            } else if (referer.contains("/member/store")) {
                return "redirect:/member/store/cart/list";
            }
        }
        // 기본 fallback
        return "redirect:/member/main";
    }

    @GetMapping("/receipt")
    public String goreceipt(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        System.out.println("이전 페이지: " + referer);
        if (referer != null) {
            if (referer.contains("/roommenu")) {
                return "redirect:/roommenu/orderList";
            } else if (referer.contains("/member/store")) {
                return "redirect:/member/store/order/list";
            }
        }
        // 기본 fallback
        return "redirect:/member/main";
    }

    @ResponseBody
    @GetMapping("/getlength")
    public ResponseEntity getlength(HttpServletRequest request){
        String referer = request.getHeader("Referer");
//        System.out.println("이전 페이지: " + referer);
        Member member = zzService.hotelroomNumToMember(hotelroomNum);
        String email = member.getMemberEmail();
        if (referer != null) {
            if (referer.contains("/roommenu")) {
                Integer totalCartItemCount = roomMenuCartService.getTotalCartItemCount(email);
                return new ResponseEntity<>(totalCartItemCount, HttpStatus.OK);
            } else if (referer.contains("/member/store")) {
                return new ResponseEntity<>(storecartService.getCartList(hotelroomNum).size(),HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    }
}
