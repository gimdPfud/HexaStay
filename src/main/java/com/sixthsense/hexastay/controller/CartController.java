/***********************************************
 * 클래스명 : CartController
 * 기능 :
 * 작성자 :
 * 작성일 : 2025-04-15
 * 수정 : 2025-04-15
 * ***********************************************/
package com.sixthsense.hexastay.controller;

import com.sixthsense.hexastay.entity.RoomService;
import com.sixthsense.hexastay.repository.MemberRepository;
import com.sixthsense.hexastay.service.RoomMenuCartService;
import com.sixthsense.hexastay.service.StorecartService;
import com.sixthsense.hexastay.service.impl.RoomServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;

@RequiredArgsConstructor
@Controller
public class CartController {
    private final StorecartService storecartService;
    private final RoomMenuCartService roomMenuCartService;
    private final MemberRepository memberRepository;
    private final RoomServiceImpl roomService;

    @GetMapping("/gocart")
    public String gocart(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        System.out.println("이전 페이지: " + referer);
        if (referer != null) {
            if (referer.contains("/roommenu")) {
                return "redirect:/roommenu/cartlist";//todo 윤겸님 장바구니 경로로 설정해주세요!
            } else if (referer.contains("/member/store")) {
                return "redirect:/member/store/cart/list";
            }
        }
        // 기본 fallback
        return "redirect:/member/main";
    }

    @ResponseBody
    @GetMapping("/getlength")
    public ResponseEntity getlength(HttpServletRequest request, Principal principal){ //todo 일단 로그인으로...?
        String referer = request.getHeader("Referer");
        System.out.println("이전 페이지: " + referer);
        if(principal==null){
            return null;
        }
        String email = principal.getName();
        Long memberNum = memberRepository.findByMemberEmail(email).getMemberNum();
//        roomService.getHotelRoomsByMember(memberNum,)

        if (referer != null) {
            if (referer.contains("/roommenu")) {
                Integer totalCartItemCount = roomMenuCartService.getTotalCartItemCount(email);
                return new ResponseEntity<>(totalCartItemCount, HttpStatus.OK);
            } else if (referer.contains("/member/store")) {
//                return new ResponseEntity<>(storecartService.getCartList(hotelroomNum).size());
            }
        }
        return null;
    }
}
