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
import jakarta.servlet.http.HttpSession;
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
import java.util.*;

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
                                      @RequestParam("orderstoreMessage") String orderstoreMessage,
                                      Principal principal, HttpSession session){
        if (principal == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Long roomNum = (Long) session.getAttribute("roomNum");

        if(cartitemidList==null||cartitemidList.isEmpty()){
            return ResponseEntity.badRequest().body("장바구니가 비었습니다.");
        }
        for (Long itemid : cartitemidList) {
            if(!storecartService.validCartItemOwner(itemid,roomNum)){
                return new ResponseEntity<>("잘못된 접근입니다.",HttpStatus.FORBIDDEN);
            }
        }
        int result = orderstoreService.insert(cartitemidList, roomNum, orderstoreMessage);
        if (result==1) {
            log.info("정상주문되었습니다.");
            storecartService.clearCartItems(roomNum);
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
    @GetMapping("/member/store/order/list") // 클래스 레벨 /member/store/order 와 결합됨
    public String clientOrderList(Model model, HttpSession session,
                                  @PageableDefault(sort = "orderstoreNum", direction = Sort.Direction.DESC) Pageable pageable,
                                  Principal principal, Locale locale){
        log.info("오더리스트 다국어 서비스 진입: {}", locale);
        if (principal == null) {
            return "sample/qrcamera";
        }

        Long roomNum = (Long) session.getAttribute("roomNum");
        if (roomNum == null) {
            // 에러 처리
            model.addAttribute("errorMessage", "객실 정보를 찾을 수 없습니다.");
            model.addAttribute("currentLang", locale.getLanguage()); // 에러 페이지에서도 언어 코드 전달
            return "error/error";
        }

        Page<OrderstoreViewDTO> list = orderstoreService.getOrderList(roomNum, pageable, locale);
        model.addAttribute("list", list);

        // 옵션용 Map 계산
        Map<Long, List<String>> optionMap = new HashMap<>();
        list.getContent().forEach(order -> { // Page 객체에서 콘텐츠 리스트 가져오기
            if (order.getOrderstoreitemDTOList() != null) {
                order.getOrderstoreitemDTOList().forEach(dto -> {
                    if (dto.getStoremenuOptions() != null && !dto.getStoremenuOptions().isBlank()) {
                        try {
                            List<String> options = Arrays.stream(dto.getStoremenuOptions().split(","))
                                    .map(option -> {
                                        List<String> optionInfos = Arrays.stream(option.split(":")).map(String::trim).toList();
                                        if (optionInfos.size() >= 3) {
                                            return optionInfos.get(1) + " (" + optionInfos.get(2) + " " + "원" + ")";
                                        } else {
                                            log.warn("유효성검사가 올바르지 않음. {}: {}", dto.getOrderstoreitemNum(), option);
                                            return option;
                                        }
                                    }).toList();
                            optionMap.put(dto.getOrderstoreitemNum(), options);
                        } catch (Exception e) {
                            log.error("패싱 오류, 멤버 Num을 찾을 수 없음.", dto.getOrderstoreitemNum(), e.getMessage());
                        }
                    }
                });
            }
        });
        model.addAttribute("optionMap", optionMap); // 옵션 맵 모델에 추가

        model.addAttribute("currentLang", locale.getLanguage());


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
        List< StoreDTO> storelist = storeService.getAllList(adminDTO);//됨
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

        List< StoreDTO> storelist = storeService.getAllList(adminDTO);

        List<OrderstoreDTO> list = orderstoreService.getOrderedList(storeNum);

        //옵션용 Map 이름가격 > 하나의메뉴
        Map<Long, List<String>> optionMap = new HashMap<>();
        list.forEach(order->{
            order.getOrderstoreitemDTOList().forEach(dto->{ //메뉴1개
                if(dto.getStoremenuOptions()!=null&&!dto.getStoremenuOptions().isBlank()) {
                    List<String> options = Arrays.stream(dto.getStoremenuOptions().split(",")).toList();
                    options = options.stream().map(option->{//옵션1개
                        List<String> optionInfos = Arrays.stream((option.split(":"))).toList();
                        option = optionInfos.get(1) + " (" + optionInfos.get(2) + "원)";
                        return option;
                    }).toList();
                    optionMap.put(dto.getOrderstoreitemNum(),options);
                }
            });
        });

        log.info("스토어넘버 넘어왔니? "+storeNum);
        log.info("서비스에서 찾아옴? "+list.size());
        list.forEach(log::info);

        model.addAttribute("list",list);
        model.addAttribute("storeList",storelist);
        model.addAttribute("optionMap",optionMap);
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
//    @GetMapping("/admin/store/order/paid/{orderNum}") 이거 그냥 insert에서 paid 설정하기로 함
//    public ResponseEntity paidOrder(@PathVariable(value = "orderNum") Long orderNum){
//        try {
//            log.info("paid로 변환 시작");
//            osService.paid(orderNum);
//            log.info("됨");
//        } catch (EntityNotFoundException e){
//            log.info("못찾음");
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        } catch (Exception e) {
//            log.info("왜안됨");
//            throw new RuntimeException(e);
//        }
//        return new ResponseEntity<>(HttpStatus.OK);
//    }
//    @ResponseBody
//    @GetMapping("/member/store/order/getlastorder")
//    public ResponseEntity getlastorder(Principal principal){
//        if(principal == null){return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);}
//        Long hotelroomNum = zzService.principalToHotelroomNum(principal);
//        Long orderid = orderstoreService.getLastOrder(hotelroomNum);
//        if(orderid==null){
//            return new ResponseEntity<>("다시 시도해주세요.",HttpStatus.BAD_REQUEST);
//        }
//        return new ResponseEntity(orderid,HttpStatus.OK);
//    }
}
