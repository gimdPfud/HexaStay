package com.sixthsense.hexastay.controller;

/* 클래스명 : RoomMenuController
 * 기능 : 룸서비스(메뉴)와 관련된 컨트롤러
 * 작성자 : 홍길동
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

    @GetMapping("/roommenu/mainpage")
    public String RoomServiceMain() {
        log.info("메인페이지 진입");

        return "roommenu/mainpage";
    }

    @GetMapping("/roommenu/mainpageA")
    public String RoomServiceMainA() {
        log.info("메인페이지 진입A");

        return "roommenu/mainpageA";
    }

    @GetMapping("/roommenu/cart")
    public String cart() {
        log.info("cartA");

        return "roommenu/cart";
    }

    @GetMapping("/roommenu/insert")
    public String RoomServiceItemGet() {
        log.info("등록페이지 get 진입");

        return "roommenu/insert";
    }

    @PostMapping("/roommenu/insert")
    public String RoomServicePost(RoomMenuDTO roomMenuDTO) {
        log.info("등록페이지 post 진입");

        // 서비스를 통해 내부처리
        roomMenuService.insert(roomMenuDTO);

        return "redirect:/roommenu/list";
    }

    @GetMapping("/roommenu/list")
    public String RoomMenuList(Model model, Pageable pageable) {
        log.info("리스트 컨트롤러 진입");

        Page<RoomMenuDTO> roomMenuDTOPage =
                roomMenuService.RoomMenuList(pageable);

        model.addAttribute("list", roomMenuDTOPage);

        return "roommenu/list";
    }

    @GetMapping("/roommenu/read")
    public String roomMenuRead(Long num, Model model) {
        log.info("상세보기 컨트롤러 진입" + num);

        RoomMenuDTO roomMenuDTO = roomMenuService.read(num);

        model.addAttribute("roomMenuDTO", roomMenuDTO);
        log.info("모델로 받은 dto" + roomMenuDTO);

        return "roommenu/read";

    }

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

    @PostMapping("/roommenu/delete")
    public String roomMenuDelete(Long num){
        log.info("삭제 컨트롤러 진입" + num);

        roomMenuService.delete(num);

        return "redirect:/roommenu/list";

    }


    @GetMapping("/roommenu/orderpage")
    public String orderpage(){


        return "roommenu/orderpage";
    }
}