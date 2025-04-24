///***********************************************
// * 클래스명 : StoreClientController
// * 기능 : principal을 사용한 고객전용 페이지
// * 작성자 : 김예령
// * 작성일 : 2025-04-22
// * 수정 : 2025-04-22
// * ***********************************************/
//package com.sixthsense.hexastay.controller;
//
//import com.sixthsense.hexastay.dto.StoreDTO;
//import com.sixthsense.hexastay.dto.StoremenuDTO;
//import com.sixthsense.hexastay.service.StoreService;
//import com.sixthsense.hexastay.service.StorecartService;
//import com.sixthsense.hexastay.service.StoremenuService;
//import com.sixthsense.hexastay.service.ZzService;
//import jakarta.persistence.EntityNotFoundException;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.log4j.Log4j2;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import java.security.Principal;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Controller
//@RequiredArgsConstructor
//@Log4j2
//@RequestMapping("/member/store")/*지금 일단 로그인때문에... 멤버 밑에다 껴둠.*/
//public class StoreClientController {
//    private final StoreService storeService;
//    private final ZzService zzService;
//    private final StoremenuService storemenuService;
//    private final StorecartService storecartService;
//
//
///* 1. 스토어 목록 보기
//        get. */
//    @GetMapping("/list")
//    public String list(Model model, Pageable pageable, Principal principal){
//        if(principal==null){
//            return "redirect:/member/login";
//        }
//        Long hotelroomNum = null;
//        try {
//            hotelroomNum = zzService.principalToHotelroomNum(principal);
//        }catch (EntityNotFoundException e){
//            return "redirect:/member/login";
//        }
//        Page<StoreDTO> storeDTOPage = storeService.clientlist(pageable);
//        log.info("스토어 목록 불러왔니?? : "+storeDTOPage.getSize());
//        model.addAttribute("totalCartItemCount",storecartService.getCartList(hotelroomNum).size());
//        model.addAttribute("list",storeDTOPage);
//
////        storeService.
//        return "mobilestore/list";
//    }
//
///* 2. 스토어 상세 보기
//        get. */
//    @GetMapping("/read/{storeNum}")
//    public String storeRead(@PathVariable Long storeNum, Model model){
//        StoreDTO storeDTO = storeService.read(storeNum);
//        model.addAttribute("data", storeDTO);
//        return "mobilestore/read";
//    }
//
///* 3. 스토어메뉴 목록 보기 (rest)
//        get. */
//    @ResponseBody
//    @GetMapping("/menu/list/{storeNum}")
//    public ResponseEntity menulist(@PathVariable Long storeNum){
//        List<StoremenuDTO> storemenuDTOList = storemenuService.list(storeNum);
//        if(storemenuDTOList.isEmpty()){
//            return new ResponseEntity<>("목록을 불러올 수 없습니다.", HttpStatus.NOT_FOUND);
//        }else {
//            return new ResponseEntity<>(storemenuDTOList, HttpStatus.OK);
//        }
//    }
//
///* 4. 스토어메뉴 상세 보기
//        get. */
//    @GetMapping("/menu/read/{storemenuNum}")
//    public String menuRead(@PathVariable Long storemenuNum, Model model){
//        StoremenuDTO storemenuDTO = storemenuService.read(storemenuNum);
//        model.addAttribute("data",storemenuDTO);
//        return "mobilestore/menuread";
//    }
//
//    /*5. 스토어 좋아요~ 또는 싫어요~?*/
//    @ResponseBody
//    @GetMapping("/like/{storeNum}")
//    public ResponseEntity liketoggle(@PathVariable Long storeNum, Principal principal){
//        if(principal==null){
//            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
//        }
//        String email = principal.getName();
//        try {
//            storeService.storeLiketoggle(storeNum, email);
//            return new ResponseEntity<>(HttpStatus.OK);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @ResponseBody
//    @GetMapping("/like/list/{storeNum}")
//    public ResponseEntity likeList(@PathVariable Long storeNum, Principal principal){
//        if(principal==null){
//            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
//        }
//        try {
//            Long likes = storeService.getStoreLikeCount(storeNum);
//            boolean check = storeService.isLiked(storeNum, principal.getName());
//            Map<String,Object> datas = new HashMap<>();
//            datas.put("likes",likes);
//            datas.put("check",check);
//            return new ResponseEntity<>(datas,HttpStatus.OK);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//}