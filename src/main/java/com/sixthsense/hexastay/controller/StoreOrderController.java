/***********************************************
 * 클래스명 : StoreOrderController
 * 기능 :
 * 작성자 :
 * 작성일 : 2025-04-09
 * 수정 : 2025-04-09
 * ***********************************************/
package com.sixthsense.hexastay.controller;

import com.sixthsense.hexastay.dto.OrderstoreViewDTO;
import com.sixthsense.hexastay.service.OrderstoreService;
import com.sixthsense.hexastay.service.StorecartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@Log4j2
@RequiredArgsConstructor
@Controller
public class StoreOrderController {
    private final OrderstoreService orderstoreService;
    private final StorecartService storecartService;
    /* 6. 결제하기
        get? post? */
    /*1. 주문하기 (등록)*/
    //장바구니에서 주문 버튼을 누르면 주문확인창(결제창)으로 이동
    @PostMapping("/member/store/order/insert")
    public String orderInsert(List<Long> cartitemidList, Model model){
//        if(principal==null){
//            return "redirect:/member/login";
//        }
        if(cartitemidList==null||cartitemidList.isEmpty()){
            return "redirect:/member/store/cart";
        }
//        for (Long itemid : cartitemidList) {
//            if(!storecartService.validCartItemOwner(itemid,principal.getName())){
//                return "redirect:/member/logout";
//            }
//        }
//        int result = orderstoreService.insert(cartitemidList, principal.getName());
        /*hotelroomNum이 있다고 가정.... 왜? QR찍을때 받으니까!!...*/
        Long hotelroomNum = 9L; // todo 이거 어떻게 받아오는지 나중에 다시 고쳐야 함.
        int result = orderstoreService.insert(cartitemidList, hotelroomNum);
        if(result==1){
            log.info("정상주문되었습니다.");
            List<OrderstoreViewDTO> list = orderstoreService.getOrderList(hotelroomNum);
            model.addAttribute("list",list);
            return "mobilestore/order/list";
        } else if (result ==2) {
            log.info("숙박 정보를 찾을 수 없습니다.");
            return null;
        } else if (result==3) {
            log.info("장바구니 아이템을 찾을 수 없습니다.");
            return null;
        }else {
            return null;
        }
    }
    /*2. 내역보기 (목록)
     *   todo 스토어관리자용 목록도 만들기. => 보고 만들기/취소 토글도 해야함. */
//    @GetMapping("/member/store/order/list") 근데주문내역은 일회용이잔아
//    @GetMapping("/admin/store/order/list")
//    @GetMapping("/admin/store/order/modify")
}
