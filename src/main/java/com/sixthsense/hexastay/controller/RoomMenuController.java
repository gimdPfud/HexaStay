package com.sixthsense.hexastay.controller;

/* 클래스명 : RoomMenuController
 * 기능 : 룸서비스(메뉴)와 관련된 컨트롤러
 *        룸서비스의 메뉴를 관리하는 다양한 페이지를 처리하는 컨트롤러.
 *        메뉴 목록 조회, 상세보기, 등록, 수정, 삭제 등의 기능을 담당
 * 작성자 : 김윤겸
 * 작성일 : 2025-04-01
 * 수정일 : 2025-00-00 입출력변수설계 : 김윤겸 */

import com.sixthsense.hexastay.dto.RoomMenuDTO;
import com.sixthsense.hexastay.service.RoomMenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
@Log4j2
public class RoomMenuController {

    private final RoomMenuService roomMenuService;

    /**************************************************
     * 메인 페이지
     * 기능 : 룸서비스 메인 페이지로 이동
     **************************************************/

    @GetMapping("/roommenu/mainpage")
    public String RoomServiceMain() {
        log.info("메인페이지 진입");

        return "roommenu/mainpage";
    }

    /**************************************************
     * 다른 메인 페이지 (또 다른 스타일이나 레이아웃의 메인 페이지)
     * 기능 : QR 코드를 찍으면 인증키가 나오는 화면
     **************************************************/

    @GetMapping("/roommenu/mainpageA")
    public String RoomServiceMainA() {
        log.info("메인페이지 진입A");

        return "roommenu/mainpageA";
    }

    /**************************************************
     * 장바구니 페이지
     * 기능 : 장바구니 페이지로 이동
     **************************************************/

    @GetMapping("/roommenu/cart")
    public String cart() {
        log.info("cartA");

        return "roommenu/cart";
    }

    /**************************************************
     * 룸서비스 메뉴 등록 페이지 (GET)
     * 기능 : 룸서비스 메뉴 등록 페이지로 이동
     **************************************************/

    @GetMapping("/roommenu/insert")
    public String RoomServiceItemGet() {
        log.info("등록페이지 get 진입");

        return "roommenu/insert";
    }

    /**************************************************
     * 룸서비스 메뉴 등록 처리 (POST)
     * 기능 : 사용자가 작성한 메뉴 등록 정보를 처리하여 저장
     **************************************************/

    @PostMapping("/roommenu/insert")
    public String RoomServicePost(RoomMenuDTO roomMenuDTO) {
        log.info("등록페이지 post 진입");

        // 서비스를 통해 내부처리
        roomMenuService.insert(roomMenuDTO);

        return "redirect:/roommenu/list";
    }

    /**************************************************
     * 룸서비스 메뉴 리스트 페이지
     * 기능 : 룸서비스 메뉴 리스트를 페이지네이션 처리하여 조회
     **************************************************/

    @GetMapping("/roommenu/list")
    public String RoomMenuList(Model model, Pageable pageable) {
        log.info("리스트 컨트롤러 진입");

        Page<RoomMenuDTO> roomMenuDTOPage =
                roomMenuService.RoomMenuList(pageable);

        model.addAttribute("list", roomMenuDTOPage);

        return "roommenu/list";
    }

    /**************************************************
     * 룸서비스 메뉴 상세 보기 페이지
     * 기능 : 특정 메뉴의 상세 정보를 보여줌
     **************************************************/

    @GetMapping("/roommenu/read")
    public String roomMenuRead(Long num, Model model) {
        log.info("상세보기 컨트롤러 진입" + num);

        RoomMenuDTO roomMenuDTO = roomMenuService.read(num);

        model.addAttribute("roomMenuDTO", roomMenuDTO);
        log.info("모델로 받은 dto" + roomMenuDTO);

        return "roommenu/read";

    }

    /**************************************************
     * 룸서비스 메뉴 수정 페이지 (GET)
     * 기능 : 메뉴 수정 페이지로 이동하고, 해당 메뉴의 정보를 가져와 모델에 추가
     **************************************************/

    @GetMapping("/roommenu/modify")
    public String roomMenuModifyGet(Long num, Model model, RoomMenuDTO roomMenuDTO) {
        log.info("Get 수정 컨트롤러 진입" + num);

        try {

            RoomMenuDTO menuDTO = roomMenuService.read(num);

            if (menuDTO != null){
                model.addAttribute("menuDTO", menuDTO);
            }else{
                log.info("해당 메뉴가 없습니다.");
                return "redirect:/roommenu/list";
            }

        } catch (Exception e) {

            log.info("업데이트 GET 컨트롤러 실패");
            return "redirect:/roommenu/list";

        }
        return "roommenu/modify";
    }

    /**************************************************
     * 룸서비스 메뉴 수정 처리 (POST)
     * 기능 : 메뉴 수정 정보를 처리하여 업데이트
     **************************************************/

    @PostMapping("/roommenu/modify")
    public String roomMenuModifyPost(Long num, RoomMenuDTO roomMenuDTO) {

        try {
            log.info("Post 수정 컨트롤러 진입: " + roomMenuDTO.getRoomMenuNum());

            roomMenuService.modify(roomMenuDTO);

        } catch (Exception e) {

            log.info("업데이트 POST 컨트롤러 실패");
            return "redirect:/roommenu/list";

        }

        return "redirect:/roommenu/read?num=" + roomMenuDTO.getRoomMenuNum();
    }

    /**************************************************
     * 룸서비스 메뉴 삭제
     * 기능 : 특정 메뉴를 삭제
     **************************************************/

    @PostMapping("/roommenu/delete")
    public String roomMenuDelete(Long num){
        log.info("삭제 컨트롤러 진입" + num);

        roomMenuService.delete(num);

        return "redirect:/roommenu/list";

    }

    /**************************************************
     * 룸서비스 주문 페이지
     * 기능 : 주문 페이지로 이동
     **************************************************/

    @GetMapping("/roommenu/orderpage")
    public String orderpage(){

        return "roommenu/orderpage";
    }
}