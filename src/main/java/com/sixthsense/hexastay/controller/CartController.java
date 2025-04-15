/***********************************************
 * 클래스명 : CartController
 * 기능 :
 * 작성자 :
 * 작성일 : 2025-04-15
 * 수정 : 2025-04-15
 * ***********************************************/
package com.sixthsense.hexastay.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CartController {

    @GetMapping("/gocart")
    public String gocart(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        System.out.println("이전 페이지: " + referer);
        if (referer != null) {
            if (referer.contains("/roommenu")) {
                return "redirect:/";//todo 윤겸님 장바구니 경로로 설정해주세요!
            } else if (referer.contains("/member/store")) {
                return "redirect:/member/store/cart/list";
            }
        }
        // 기본 fallback
        return "redirect:/member/main";
    }
}
