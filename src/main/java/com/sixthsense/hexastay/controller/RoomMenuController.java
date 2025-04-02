package com.sixthsense.hexastay.controller;
import com.sixthsense.hexastay.dto.RoomMenuDTO;
import com.sixthsense.hexastay.service.RoomMenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/* 클래스명 : RoomMenuController
 * 기능 : 룸서비스(메뉴)와 관련된 컨트롤러
 * 작성자 : 홍길동
 * 작성일 : 2025-04-01
 * 수정일 : 2025-00-00 입출력변수설계 : 김윤겸 */

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
    public Page<RoomMenuDTO> getRoomMenuList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("리스트 컨트롤러 진입");

        Pageable pageable = PageRequest.of(page, size);
        return roomMenuService.RoomMenuList(pageable);
    }

    @GetMapping("/roommenu/read")
    public String roomMenuRead(Integer num, Model model){
        log.info("상세보기 컨트롤러 진입" + num);

        RoomMenuDTO roomMenuDTO = roomMenuService.read(num);

        model.addAttribute("roomMenuDTO", roomMenuDTO);
        log.info("모델로 받은 dto" + roomMenuDTO );

        return "roommenu/read";




    }

    @GetMapping("/roommenu/orderpage")
    public String orderpage(){


        return "roommenu/orderpage";
    }
}
