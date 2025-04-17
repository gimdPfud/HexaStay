package com.sixthsense.hexastay.controller;

/* 클래스명 : RoomMenuController
 * 기능 : 룸서비스(메뉴)와 관련된 컨트롤러
 *        룸서비스의 메뉴를 관리하는 다양한 페이지를 처리하는 컨트롤러.
 *        메뉴 목록 조회, 상세보기, 등록, 수정, 삭제 등의 기능을 담당
 * 작성자 : 김윤겸
 * 작성일 : 2025-04-01
 * 수정일 : 2025-00-00 입출력변수설계 : 김윤겸 */

import com.sixthsense.hexastay.dto.MemberDTO;
import com.sixthsense.hexastay.dto.RoomMenuDTO;
import com.sixthsense.hexastay.service.RoomMenuCartService;
import com.sixthsense.hexastay.service.RoomMenuService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.Principal;
import java.util.Locale;
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

//    @GetMapping("/roommenu/cartlist")
//    public String cart() {
//        log.info("roommenuCart Get 방식 진입");
//
//        return "roommenu/cartlist";
//    }

    /***************************************************
     * 메소드명   : roomMenuReadA
     * 기능      : 특정 메뉴 아이템의 상세 정보를 조회하여 화면에 전달
     * 작성자    : 김윤겸
     * 작성일    : 2025-04-06
     * 수정일    :
     * 설명      : 전달된 `num` 파라미터를 통해 메뉴 아이템의 상세 정보를 조회하고
     *            `roommenu/read` 뷰를 반환
     * 파라미터   : `num` - 메뉴 아이템의 ID (Long)
     * 반환값     : String - `roommenu/read` 뷰 이름
     ****************************************************/

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
    public String RoomServiceItemGet(Principal principal) {
        log.info("등록페이지 get 컨트롤러 진입");
        return "roommenu/insert";
    }

    /**************************************************
     * 룸서비스 메뉴 등록 처리 (POST)
     * 기능 : 사용자가 작성한 메뉴 등록 정보를 처리하여 저장
     * 수정일 : 2025-04-08
     **************************************************/

    @PostMapping("/roommenu/insert")
    public String RoomServicePost(RoomMenuDTO roomMenuDTO, Principal principal) throws IOException {
        log.info("등록페이지 post 컨트롤러 진입");
        log.info("로그인 : " + principal.getName());
        log.info("ㅇㅇ"+ roomMenuDTO.getRoomMenuImage().getOriginalFilename());
        String memberName = principal.getName();  // 로그인한 사용자의 이름 (또는 ID)


        if (principal == null) {
            // 로그인하지 않은 경우 로그인 페이지로 리다이렉트
            return "redirect:/admin/login";  // 로그인 페이지 URL로 변경
        }

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
    public String RoomMenuList(@PageableDefault(size = 8) Pageable pageable,
                               @RequestParam(value="type", required = false, defaultValue = "") String type,
                               @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
                               @RequestParam(value = "category", required = false) String category, // @RequestParam 추가
                               Model model) {
        log.info("리스트 컨트롤러 진입");

        Page<RoomMenuDTO> roomMenuDTOPage =
                roomMenuService.RoomMenuList(pageable, type, keyword, category);

        Map<String, Integer> pageInfo = Pagination(roomMenuDTOPage);

        model.addAttribute("list", roomMenuDTOPage);
        model.addAttribute("type", type);
        model.addAttribute("keyword", keyword);
        model.addAttribute("category", category);
        model.addAllAttributes(pageInfo);

        return "roommenu/list";
    }

    /***************************************************
     *
     * 메소드명   : roomMenuOrderRead
     * 기능      : 장바구니에서 특정 메뉴 아이템의 상세 정보를 조회하여 화면에 전달
     * 작성자    : 김윤겸
     * 작성일    : 2025-04-06
     * 수정일    :
     *
     * 설명      : 클라이언트로부터 전달된 `num` 파라미터(메뉴 아이템의 ID)를 통해
     *            해당 아이템의 상세 정보를 조회하여 모델에 추가하고,
     *            `orderpage/orderread` 뷰를 반환합니다.
     *
     * 파라미터   : `num` - 조회할 메뉴 아이템의 ID (Long)
     * 반환값     : String - `orderpage/orderread` 뷰 이름
     *
     ****************************************************/

    @GetMapping("/roommenu/orderpage/orderread")
    public String roomMenuOrderRead(@RequestParam Long num, Model model) {
        log.info("상세보기 컨트롤러 진입: " + num);

        RoomMenuDTO roomMenuDTO = roomMenuCartService.read(num);
        model.addAttribute("roomMenuDTO", roomMenuDTO);
        log.info("모델로 받은 dto: " + roomMenuDTO);

        return "roommenu/orderpage/orderread"; // 뷰 이름
    }


    @GetMapping("/roommenu/read")
    public String roomMenuRead(Long num, Model model) {

        RoomMenuDTO roomMenuDTO = roomMenuCartService.read(num);

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
     * 수정일 : 2025-04-08 - 프린시퀄 추가, 2025-04-09 : 기본 이미지 설정
     **************************************************/

    @PostMapping("/roommenu/modify")
    public String roomMenuModifyPost(Long num, RoomMenuDTO roomMenuDTO, Principal principal, RedirectAttributes redirectAttributes) {

        String memberName = principal.getName();  // 로그인한 사용자의 이름 (또는 ID)
        log.info("로그인한 사용자: " + memberName);
        log.info(principal.toString());

        try {
            log.info("Post 수정 컨트롤러 진입: " + roomMenuDTO.getRoomMenuNum());

            roomMenuService.modify(roomMenuDTO); // 서비스 호출
            redirectAttributes.addFlashAttribute("msg", "수정 완료");


        } catch (Exception e) {

            redirectAttributes.addFlashAttribute("msg", "수정 실패: " + e.getMessage());
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
     * 기능 : 주문 페이지로 이동 및 상품 정보 조회
     **************************************************/

    @GetMapping("/roommenu/orderpage/read")
    public String roomMenuOrderpageRead(String email, Model model ,RedirectAttributes redirectAttributes) {
        log.info("상품정보 페이지" + email);
        // email을 통해서 상품 정보를 가져오자.

        if (email == null) {
            return "redirect:member/login";
        }

        //정보가져오기
        try {
            RoomMenuDTO roomMenuDTO =
                    roomMenuCartService.RoomMenuCartRead(email);
            //가져온 상품 보기
            log.info("상품정보" + roomMenuDTO);

            model.addAttribute("roomMenuDTO", roomMenuDTO);

            return "roommenu/orderpage/orderread";

        } catch (EntityNotFoundException e) {

            redirectAttributes.addFlashAttribute("msg", "존재하지 않는 상품입니다.");
            return "redirect:/roommenu/orderpage";
        }
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
                            Principal principal,
                            Locale locale, // ✅ 추가
                            Model model) {

        log.info("주문페이지 컨트롤러 리스트 진입");
        log.info("로그인한 사용자: " + principal.getName());

        String email = principal.getName(); // 로그인한 사용자의 이메일
        Integer totalCartItemCount = roomMenuCartService.getTotalCartItemCount(email);

        // ✅ 로케일 처리
        String lang = locale.getLanguage(); // ex) "ko", "en"

        // ✅ 다국어 적용된 서비스 호출
        Page<RoomMenuDTO> roomMenuList = roomMenuCartService.RoomMenuList(pageable, type, keyword, category, locale);

        Map<String, Integer> pageInfo = Pagination(roomMenuList);

        model.addAttribute("list", roomMenuList);
        model.addAttribute("type", type);
        model.addAttribute("keyword", keyword);
        model.addAttribute("category", category);
        model.addAttribute("totalCartItemCount", totalCartItemCount);
        model.addAllAttributes(pageInfo);

        return "/roommenu/orderpage";

    }


}

///**************************************************
// * 주문 페이지 조회 및 필터링
// * 기능: 사용자로부터 전달받은 검색 조건(type, keyword, category)을 기준으로
// *       룸서비스 메뉴 목록을 조회하고, 페이지네이션을 적용하여 화면에 표시
// *       등록일 : 2025-04-07
// *       수정일 : 2025-04-07
// **************************************************/
//
//// 좋아요 요청
//@PostMapping("/roomMenu/orderpage/like/{roomMenuNum}")
//@ResponseBody
//public ResponseEntity<Integer> RoomMenuLikeService(@PathVariable("roomMenuNum") Long roomMenuNum) {
//    int likes = roomMenuService.roomMenuLike(roomMenuNum);
//    return ResponseEntity.ok(likes);
//}
//
//// 좋아요 취소
//@PostMapping("/roomMenu/orderpage/unlike/{roomMenuNum}")
//@ResponseBody
//public ResponseEntity<Integer> RoomMenuUnLikeService(@PathVariable("roomMenuNum") Long roomMenuNum) {
//    int likes = roomMenuService.roomMenuLikeCancel(roomMenuNum);
//    return ResponseEntity.ok(likes);
//}
//
//// 싫어요 요청
//@PostMapping("/roomMenu/orderpage/dislike/{roomMenuNum}")
//@ResponseBody
//public ResponseEntity<Integer> RoomMenuDisLikeService(@PathVariable("roomMenuNum") Long roomMenuNum) {
//    int dislikes = roomMenuService.roomMenuDisLike(roomMenuNum);
//    return ResponseEntity.ok(dislikes);
//}
//
//// 싫어요 취소
//@PostMapping("/roomMenu/orderpage/undislike/{roomMenuNum}")
//@ResponseBody
//public ResponseEntity<Integer> RoomMenuUnDisLikeService(@PathVariable("roomMenuNum") Long roomMenuNum) {
//    int dislikes = roomMenuService.roomMenuDisLikeCancel(roomMenuNum);
//    return ResponseEntity.ok(dislikes);
//}