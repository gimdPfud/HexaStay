/***********************************************
 * 클래스명 : StoreOrderController
 * 기능 :
 * 작성자 :
 * 작성일 : 2025-04-09
 * 수정 : 2025-04-09
 * ***********************************************/
package com.sixthsense.hexastay.controller;

import com.sixthsense.hexastay.dto.AdminDTO;
import com.sixthsense.hexastay.dto.OrderstoreDTO;
import com.sixthsense.hexastay.dto.OrderstoreViewDTO;
import com.sixthsense.hexastay.dto.StoreDTO;
import com.sixthsense.hexastay.service.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Log4j2
@RequiredArgsConstructor
@Controller
public class StoreOrderController {
    private final OrderstoreService orderstoreService;
    private final StorecartService storecartService;
    private final AdminService adminService;
    private final StoreService storeService;
    private final ZzService zzService;



    /*1. 주문하기 (등록)*/
    //장바구니에서 주문 버튼을 누르면 주문확인창(결제창)으로 이동
    @ResponseBody
    @PostMapping("/member/store/order/insert")
    public ResponseEntity orderInsert(@RequestParam("items") List<Long> cartitemidList,
                                      @RequestParam("orderstoreMessage") String orderstoreMessage, Principal principal){
        if(principal == null){return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);}
        Long hotelroomNum = zzService.principalToHotelroomNum(principal);
        if(cartitemidList==null||cartitemidList.isEmpty()){
            return ResponseEntity.badRequest().body("장바구니가 비었습니다.");
        }
        for (Long itemid : cartitemidList) {
            if(!storecartService.validCartItemOwner(itemid,hotelroomNum)){
                return new ResponseEntity<>("잘못된 접근입니다.",HttpStatus.FORBIDDEN);
            }
        }
        /*hotelroomNum이 있다고 가정.... 왜? QR찍을때 받으니까!!...*/
        int result = orderstoreService.insert(cartitemidList, hotelroomNum, orderstoreMessage);
        if(result==1){
            log.info("정상주문되었습니다.");
            storecartService.clearCartItems(hotelroomNum);
            return new ResponseEntity<>(HttpStatus.OK);
        } else if (result ==2) {
            return ResponseEntity.badRequest().body("숙박 정보를 찾을 수 없습니다.");
        } else if (result==3) {
            return ResponseEntity.badRequest().body("장바구니가 비었습니다.");
        }else {
            return new ResponseEntity<>("알 수 없는 오류입니다.",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    /*2. 내역보기 (목록)*/
    @GetMapping("/member/store/order/list") //근데주문내역은 일회용이잔아
    public String clientOrderList(Model model,
                                  @PageableDefault(sort = "orderstoreNum", direction = Sort.Direction.DESC) Pageable pageable, Principal principal){
        if(principal == null){return "sample/qrcamera";}
        Long hotelroomNum = zzService.principalToHotelroomNum(principal);
        Page<OrderstoreViewDTO> list = orderstoreService.getOrderList(hotelroomNum, pageable);
        model.addAttribute("list",list);
        return "mobilestore/order/list";
    }


    @GetMapping("/admin/store/order/list")
    public String adminOrderList(Principal principal,
                                 @RequestParam(required = false) Long storeNum,
                                 Model model){
        if (principal == null) {
            return "redirect:/admin/login";
        }
        AdminDTO adminDTO = adminService.adminFindEmail(principal.getName());
        if (adminDTO == null) {
            return "redirect:/admin/logout";
        }
        List< StoreDTO> storelist = storeService.getAllList();//됨
        model.addAttribute("storeList",storelist);
        return "store/orderlistForAdmin";
    }
    @PostMapping("/admin/store/order/list")
    public String adminOrderList(@RequestParam(value = "storeNum") Long storeNum, Principal principal, Model model){
        if (principal == null) {
            return "redirect:/admin/login";
        }
        AdminDTO adminDTO = adminService.adminFindEmail(principal.getName());
        if (adminDTO == null) {
            return "redirect:/admin/logout";
        }
        List< StoreDTO> storelist = storeService.getAllList();
        model.addAttribute("storeList",storelist);
        log.info("스토어넘버 넘어왔니? "+storeNum);
        List<OrderstoreDTO> list = orderstoreService.getOrderedList(storeNum);//됨?
        log.info("서비스에서 찾아옴? "+list.size());
        list.forEach(log::info);
        model.addAttribute("list",list);
        model.addAttribute("storeNum",storeNum);
        return "store/orderlistForAdmin";
    }
    @GetMapping("/admin/store/order/cancel/{orderNum}")
    public ResponseEntity cancelOrder(@PathVariable(value = "orderNum") Long orderNum){
        try {
            orderstoreService.cancel(orderNum);
        } catch (EntityNotFoundException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @GetMapping("/admin/store/order/end/{orderNum}")
    public ResponseEntity endOrder(@PathVariable(value = "orderNum") Long orderNum){
        try {
            orderstoreService.end(orderNum);
        } catch (EntityNotFoundException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @GetMapping("/admin/store/order/paid/{orderNum}")
    public ResponseEntity paidOrder(@PathVariable(value = "orderNum") Long orderNum){
        try {
            orderstoreService.paid(orderNum);
        } catch (EntityNotFoundException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @ResponseBody
    @GetMapping("/member/store/order/getlastorder")
    public ResponseEntity getlastorder(Principal principal){
        if(principal == null){return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);}
        Long hotelroomNum = zzService.principalToHotelroomNum(principal);
        Long orderid = orderstoreService.getLastOrder(hotelroomNum);
        if(orderid==null){
            return new ResponseEntity<>("다시 시도해주세요.",HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(orderid,HttpStatus.OK);
    }
}
