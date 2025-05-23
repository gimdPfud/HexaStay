/***********************************************
 * 클래스명 : CartController
 * 기능 :
 * 작성자 :
 * 작성일 : 2025-04-15
 * 수정 : 2025-04-15
 * ***********************************************/
package com.sixthsense.hexastay.controller;

import com.sixthsense.hexastay.service.RoomMenuCartService;
import com.sixthsense.hexastay.service.StorecartService;
import com.sixthsense.hexastay.service.ZzService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/qr")
    public String goQr(){
        return "sample/qrcamera";
    }

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
    public ResponseEntity getlength(HttpServletRequest request, Principal principal, HttpSession session){

        if(principal==null){return new ResponseEntity(HttpStatus.UNAUTHORIZED);}

        String referer = request.getHeader("Referer");
        String email = principal.getName();
        Long roomNum = (Long) session.getAttribute("roomNum");

        if (referer != null) {

            if (referer.contains("/roommenu")) {
                // 객실서비스 장바구니 개수 구하기
                Integer totalCartItemCount = roomMenuCartService.getTotalCartItemCount(email);
                return new ResponseEntity<>(totalCartItemCount, HttpStatus.OK);

            } else if (referer.contains("/member/store")) {
                // 스토어 장바구니 개수 구하기
                return new ResponseEntity<>(storecartService.getItemCount(roomNum),HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    }
}
