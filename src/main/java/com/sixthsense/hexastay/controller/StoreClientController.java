/***********************************************
 * 클래스명 : StoreClientController
 * 기능 : 고객전용 페이지 (1. 스토어 목록보기,
 * 2. 스토어 상세 및 3. 메뉴 목록, 4. 메뉴 상세, 5. 장바구니, 6. 외부메뉴결제까지.)
 * 작성자 : 김예령
 * 작성일 : 2025-04-07
 * 수정 : 2025-04-07
 * ***********************************************/
package com.sixthsense.hexastay.controller;

import com.sixthsense.hexastay.dto.StoreDTO;
import com.sixthsense.hexastay.dto.StoremenuDTO;
import com.sixthsense.hexastay.entity.Member;
import com.sixthsense.hexastay.service.StoreService;
import com.sixthsense.hexastay.service.StorecartService;
import com.sixthsense.hexastay.service.StoremenuService;
import com.sixthsense.hexastay.service.ZzService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/member/store")/*지금 일단 로그인때문에... 멤버 밑에다 껴둠.*/
public class StoreClientController {
    private final ZzService zzService;
    private final StoreService storeService;
    private final StoremenuService storemenuService;
    private final StorecartService storecartService;


///* 1. 스토어 목록 보기
//        get. */
//    @GetMapping("/list")
//    public String list(Model model, Pageable pageable){
//        Page<StoreDTO> storeDTOPage = storeService.clientlist(pageable);
//        model.addAttribute("totalCartItemCount",storecartService.getCartList(hotelroomNum).size());
//        model.addAttribute("list",storeDTOPage);
//        return "mobilestore/list";
//    }

    @GetMapping("/list")
    public String typelist(@RequestParam(required = false) String type,
                           @RequestParam(required = false) String keyword,
                           Model model, Pageable pageable, Principal principal, HttpSession session,
                           Locale locale) {
        if(principal==null){
            // 로그인되지 않은 경우, 기존 로직 유지
            return "sample/qrcamera";
        }
        Long hotelroomNum = zzService.sessionToHotelroomNum(session);
        Page<StoreDTO> storeDTOPage = storeService.clientlist(hotelroomNum, type, keyword, pageable, locale);
        model.addAttribute("totalCartItemCount",storecartService.getCartList(hotelroomNum).size());
        model.addAttribute("list", storeDTOPage.getContent());
        model.addAttribute("번역상황", locale.getLanguage());

//        log.info("type : "+type);
//        log.info("keyword : "+keyword);
//        log.info("보여질 언어: {}", locale.getLanguage());

        return "mobilestore/list";
    }


    /* 2. 스토어 상세 보기
        get. */
    @GetMapping("/read/{storeNum}") //
    public String storeRead(@PathVariable Long storeNum, Model model, Locale locale) {

        StoreDTO storeDTO = storeService.read(storeNum, locale);
        model.addAttribute("data", storeDTO);
        model.addAttribute("currentLang", locale.getLanguage());

//        log.info("읽을 매장의 번호: {}, 번역될 언어: {}", storeNum, locale);
//        log.info("Passing language code to view: {}", locale.getLanguage());

        return "mobilestore/read";
    }

/* 3. 스토어메뉴 목록 보기 (rest)
        get. */
    @ResponseBody
    @GetMapping("/menu/list/{storeNum}") // 기존 엔드포인트 유지
    public ResponseEntity<?> menulist(@PathVariable Long storeNum, Locale locale) { // Locale 파라미터 추가
        log.info("메뉴 목록 요청 - 스토어 ID: {}, 요청 Locale: {}", storeNum, locale.toLanguageTag());

        List<StoremenuDTO> storemenuDTOList = storemenuService.list(storeNum, locale); // locale 전달

        if (storemenuDTOList == null || storemenuDTOList.isEmpty()) { // null 체크 추가
            log.warn("스토어 ID {}에 대한 메뉴 목록을 찾을 수 없거나 비어있습니다.", storeNum);
            return new ResponseEntity<>("해당 스토어의 메뉴 목록을 불러올 수 없습니다.", HttpStatus.NOT_FOUND);
        } else {
            log.info("스토어 ID {}에 대한 메뉴 목록 {}개 반환 ({} 번역 적용)", storeNum, storemenuDTOList.size(), locale.toLanguageTag());
            return new ResponseEntity<>(storemenuDTOList, HttpStatus.OK);
        }
    }

/* 4. 스토어메뉴 상세 보기
        get. */
@GetMapping("/menu/read/{storemenuNum}")
public String menuRead(@PathVariable Long storemenuNum, Model model, Locale locale) {
    StoremenuDTO storemenuDTO = storemenuService.read(storemenuNum, locale);
//    log.info(storemenuDTO);
    model.addAttribute("data", storemenuDTO);
    model.addAttribute("currentLang", locale.getLanguage());

    log.info("Store menu read request for storemenuNum: {}, locale: {}", storemenuNum, locale); // ✅ locale 로그 추가

    return "mobilestore/menuread";
}


    /*5. 스토어 좋아요~ 또는 싫어요~?*/
    @ResponseBody
    @GetMapping("/like/{storeNum}")
    public ResponseEntity liketoggle(@PathVariable Long storeNum, Principal principal,
                                     HttpSession session){
        if(principal==null){return new ResponseEntity(HttpStatus.UNAUTHORIZED);}
        try {
            Member member = zzService.sessionToMember(session);
            storeService.storeLiketoggle(storeNum, member);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @ResponseBody
    @GetMapping("/like/list/{storeNum}")
    public ResponseEntity likeList(@PathVariable Long storeNum, Principal principal,
                                   HttpSession session){
        if(principal==null){return new ResponseEntity(HttpStatus.UNAUTHORIZED);}
        try {
//            log.info("세션에 저장된 roomNum : " + session.getAttribute("roomNum"));

            Long likes = storeService.getStoreLikeCount(storeNum);
            boolean check = storeService.isLiked(storeNum, zzService.sessionToMember(session));
            Map<String,Object> datas = new HashMap<>();
            datas.put("likes",likes);
            datas.put("check",check);
            return new ResponseEntity<>(datas,HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}