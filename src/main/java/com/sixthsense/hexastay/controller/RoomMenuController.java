package com.sixthsense.hexastay.controller;

/* 클래스명 : RoomMenuController
 * 기능 : 룸서비스(메뉴)와 관련된 컨트롤러
 *        룸서비스의 메뉴를 관리하는 다양한 페이지를 처리하는 컨트롤러.
 *        메뉴 목록 조회, 상세보기, 등록, 수정, 삭제 등의 기능을 담당
 * 작성자 : 김윤겸
 * 작성일 : 2025-04-01
 * 수정일 : 2025-00-00 입출력변수설계 : 김윤겸 */

import com.sixthsense.hexastay.dto.RoomMenuDTO;
import com.sixthsense.hexastay.service.RoomMenuCartService;
import com.sixthsense.hexastay.service.RoomMenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import static com.sixthsense.hexastay.util.PaginationUtil.Pagination;

@Controller
@RequiredArgsConstructor
@Log4j2
public class RoomMenuController {

    private final RoomMenuService roomMenuService;
    private final RoomMenuCartService roomMenuCartService;

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
        log.info("roommenuCart Get 방식 진입");

        return "roommenu/cart";
    }

    // todo : 오더페이지 상세 만들어야함
    // fixme : 따로 컨트롤러 만들어서 해야할것같음.
    // 오더페이지 상세페이지
    @GetMapping("/roomMenu/read")
    public String roomMenuReadA(Long num, Model model) {
        log.info("상세보기 컨트롤러 진입" + num);

        RoomMenuDTO roomMenuDTO = roomMenuService.read(num);

        model.addAttribute("roomMenuDTO", roomMenuDTO);
        log.info("모델로 받은 dto" + roomMenuDTO);

        return "roommenu/read";

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
     * 수정일 : 2025-04-08
     **************************************************/

    @PostMapping("/roommenu/insert")
    public String RoomServicePost(RoomMenuDTO roomMenuDTO, Principal principal) {
        log.info("등록페이지 post 진입");

        String memberName = principal.getName();  // 로그인한 사용자의 이름 (또는 ID)
        log.info("로그인한 사용자: " + memberName);
        log.info(principal.toString());

        // 서비스를 통해 내부처리
        roomMenuService.insert(roomMenuDTO);

        return "redirect:/roommenu/list";
    }

    /**************************************************
     * 룸서비스 메뉴 리스트 조회 페이지
     * 기능: 룸서비스 메뉴 목록을 검색 조건(type, keyword, category)과
     *       페이지네이션을 적용하여 조회하는 기능을 구현
     *       수정일 : 2025-04-07
     **************************************************/

    @GetMapping("/roommenu/list")
    public String RoomMenuList(Pageable pageable,
                               @RequestParam(value="type", required = false, defaultValue = "") String type,
                               @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
                               @RequestParam(value = "category", required = false) String category, // @RequestParam 추가
                               Model model) {
        log.info("리스트 컨트롤러 진입");

        Page<RoomMenuDTO> roomMenuDTOPage =
                roomMenuService.RoomMenuList(pageable, type, keyword, category);

        Map<String, Integer> pageInfo = Pagination(roomMenuDTOPage);

        for (RoomMenuDTO roomMenuDTO : roomMenuDTOPage) {
            log.info(roomMenuDTO.getRoomMenuName());
        }
        model.addAttribute("list", roomMenuDTOPage);
        model.addAttribute("type", type);
        model.addAttribute("keyword", keyword);
        model.addAttribute("category", category);
        model.addAllAttributes(pageInfo);

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
     * 수정일 : 2025-04-08 - 프린시퀄 추가
     **************************************************/

    @PostMapping("/roommenu/modify")
    public String roomMenuModifyPost(Long num, RoomMenuDTO roomMenuDTO, Principal principal) {

        String memberName = principal.getName();  // 로그인한 사용자의 이름 (또는 ID)
        log.info("로그인한 사용자: " + memberName);
        log.info(principal.toString());

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
     * 수정일 : 2025-04-08 (프린시퀄 추가)
     **************************************************/

    @PostMapping("/roommenu/delete")
    public String roomMenuDelete(Long num, Principal principal){
        log.info("삭제 컨트롤러 진입" + num);

        String memberName = principal.getName();  // 로그인한 사용자의 이름 (또는 ID)
        log.info("로그인한 사용자: " + memberName);
        log.info(principal.toString());

        roomMenuService.delete(num);
        log.info("삭제한 사용자" + memberName);

        return "redirect:/roommenu/list";

    }

    /**************************************************
     * 룸서비스 주문 페이지
     * 기능 : 주문 페이지로 이동
     **************************************************/

    @GetMapping("/roommenu/testorder")
    public String orderpage(){

        return "roommenu/testorder";
    }

    /**************************************************
     * 룸서비스 리드 페이지
     * 기능 : 리드 페이지로 이동
     **************************************************/

    @GetMapping("/roommenu/orderread")
    public String orderread(){

        return "roommenu/orderread";
    }

    /**************************************************
     * 주문 페이지 조회 및 필터링
     * 기능: 사용자로부터 전달받은 검색 조건(type, keyword, category)을 기준으로
     *       룸서비스 메뉴 목록을 조회하고, 페이지네이션을 적용하여 화면에 표시
     *       등록일 : 2025-04-07
     *       수정일 : 2025-04-07
     **************************************************/

    @GetMapping("/roommenu/orderpage")
    public String orderList(@PageableDefault(page = 0) Pageable pageable,
                            @RequestParam(value = "type", defaultValue = "") String type,
                            @RequestParam(value = "keyword", defaultValue = "") String keyword,
                            @RequestParam(value = "category", defaultValue = "") String category,
                            Model model) {
        log.info("주문페이지 컨트롤러 리스트 진입");
        log.info("type: {}", type);
        log.info("keyword: {}", keyword);
        log.info("category: {}", category);

        // 서비스 연동: 전달된 파라미터로 메뉴 리스트 필터링
        Page<RoomMenuDTO> roomMenuList = roomMenuCartService.RoomMenuList(pageable, type, keyword, category);

        // 페이지 정보 가공
        Map<String, Integer> pageInfo = Pagination(roomMenuList);

        // 로깅: 메뉴 이름 출력
        for (RoomMenuDTO roomMenuDTO : roomMenuList) {
            log.info("이것은 룸카드컨트롤러" + roomMenuDTO.getRoomMenuName());
        }

        // 값 전달 (Model)
        model.addAttribute("list", roomMenuList);
        model.addAttribute("type", type);
        model.addAttribute("keyword", keyword);
        model.addAttribute("category", category);  // 카테고리 필터링 값 전달
        model.addAllAttributes(pageInfo);

        return "/roommenu/orderpage";  // orderpage를 반환하여 뷰를 렌더링
    }



}