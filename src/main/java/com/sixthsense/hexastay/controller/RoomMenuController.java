package com.sixthsense.hexastay.controller;

/* 클래스명 : RoomMenuController
 * 기능 : 룸서비스(메뉴)와 관련된 컨트롤러
 *        룸서비스의 메뉴를 관리하는 다양한 페이지를 처리하는 컨트롤러.
 *        메뉴 목록 조회, 상세보기, 등록, 수정, 삭제 등의 기능을 담당
 * 작성자 : 김윤겸
 * 작성일 : 2025-04-01
 * 수정일 : 2025-00-00 입출력변수설계 : 김윤겸 */

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sixthsense.hexastay.dto.HotelRoomDTO;
import com.sixthsense.hexastay.dto.MemberDTO;
import com.sixthsense.hexastay.dto.RoomMenuDTO;
import com.sixthsense.hexastay.dto.RoomMenuOptionDTO;
import com.sixthsense.hexastay.entity.Room;
import com.sixthsense.hexastay.entity.RoomMenu;
import com.sixthsense.hexastay.entity.RoomMenuOption;
import com.sixthsense.hexastay.repository.RoomMenuOptionRepository;
import com.sixthsense.hexastay.repository.RoomMenuRepository;
import com.sixthsense.hexastay.service.HotelRoomService;
import com.sixthsense.hexastay.service.RoomMenuCartService;
import com.sixthsense.hexastay.service.RoomMenuService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.sixthsense.hexastay.util.PaginationUtil.Pagination;

@Controller
@RequiredArgsConstructor
@Log4j2
public class RoomMenuController {

    private final RoomMenuService roomMenuService;
    private final RoomMenuCartService roomMenuCartService;
    private final RoomMenuRepository roomMenuRepository;

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
    public String roomMenuReadA(Long num, Model model, Locale locale) {
        log.info("상세보기 Roommenu 컨트롤러 진입" + num);

        RoomMenuDTO roomMenuDTO = roomMenuService.read(num, locale);

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
                               Model model, Locale locale, boolean forUserView) {
        log.info("리스트 컨트롤러 진입");

        Page<RoomMenuDTO> roomMenuDTOPage =
                roomMenuService.RoomMenuList(pageable, type, keyword, category, locale, false);

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
    public String roomMenuOrderRead(@RequestParam Long num, Model model, Locale locale) {
        log.info("상세보기 orderpage 컨트롤러 진입: " + num);

        RoomMenuDTO roomMenuDTO = roomMenuCartService.read(num, locale, model);
        model.addAttribute("roomMenuDTO", roomMenuDTO);
        log.info("모델로 받은 dto: " + roomMenuDTO);

        return "roommenu/orderpage/orderread"; // 뷰 이름
    }


    @GetMapping("/roommenu/read")
    public String roomMenuRead(Long num, Model model, Locale locale) {
        log.info("roommenu/read get 컨트롤러 진입");


        // roomMenuDTO를 읽어옴
        RoomMenuDTO roomMenuDTO = roomMenuCartService.read(num, locale, model);

        // roomMenuDTO를 모델에 추가
        model.addAttribute("roomMenuDTO", roomMenuDTO);

        log.info("모델로 받은 dto: {}", roomMenuDTO);

        // "roommenu/read" 뷰로 리턴
        return "roommenu/read";

    }

    /**************************************************
     * 룸서비스 메뉴 수정 페이지 (GET)
     * 기능 : 메뉴 수정 페이지로 이동하고, 해당 메뉴의 정보를 가져와 모델에 추가
     **************************************************/

    @GetMapping("/roommenu/modify")
    public String roomMenuModifyGet(Long num, Model model, Locale locale) {
        log.info("Get 수정 컨트롤러 진입: " + num);

        try {
            // 🔥 옵션 포함된 메뉴 DTO를 가져옴 (locale 추가!)
            RoomMenuDTO menuDTO = roomMenuService.read(num, locale);

            if (menuDTO != null) {
                model.addAttribute("menuDTO", menuDTO);
            } else {
                log.info("해당 메뉴가 없습니다.");
                return "redirect:/roommenu/list";
            }

        } catch (Exception e) {
            log.error("업데이트 GET 컨트롤러 실패", e);
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
     * 작성일 : 2025-04-02
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
    public String orderList(@PageableDefault(page = 0, size = 100)  Pageable pageable,
                            @RequestParam(value = "type", defaultValue = "") String type,
                            @RequestParam(value = "keyword", defaultValue = "") String keyword,
                            @RequestParam(value = "category", defaultValue = "") String category,
                            Principal principal,
                            Locale locale, // ✅ 추가
                            Model model) { // 무한스크롤 비슷하게 느낌을 내기 위해서 size를 100으로 조정
        // size = Integer.MAX_VALUE 으로 다 불러올 수 있지만 데이터 많으면 오류생기니까 그냥 맵두자.

        log.info("주문페이지 컨트롤러 리스트 진입");
        log.info("로그인한 사용자: " + principal.getName());

        String email = principal.getName(); // 로그인한 사용자의 이메일
        Integer totalCartItemCount = roomMenuCartService.getTotalCartItemCount(email);


        // ✅ 로케일 처리
        String lang = locale.getLanguage(); // ex) "ko", "en"

        // ✅ 다국어 적용된 서비스 호출
        Page<RoomMenuDTO> roomMenuList =
                roomMenuService.RoomMenuList(pageable, type, keyword, category, locale, true); // <-- 유저용


        Map<String, Integer> pageInfo = Pagination(roomMenuList);

        model.addAttribute("list", roomMenuList);
        model.addAttribute("type", type);
        model.addAttribute("keyword", keyword);
        model.addAttribute("category", category);
        model.addAttribute("totalCartItemCount", totalCartItemCount);
        model.addAllAttributes(pageInfo);
        return "/roommenu/orderpage";

    }

    @GetMapping("/roommenu/dev/translation")
    public String approvalList(Model model, Pageable pageable) {
        Page<RoomMenu> pendingMenus = roomMenuRepository.findByApprovedByDevFalseAndSupportsMultilangTrue(pageable);
        model.addAttribute("pendingMenus", pendingMenus);
        return "roommenu/dev/translation"; // HTML 파일 이름
    }

    @PostMapping("/roommenu/dev/translation/{roomMenuNum}")
    public String approveMenu(@PathVariable Long roomMenuNum) {
        // 메뉴 검색
        RoomMenu menu = roomMenuRepository.findById(roomMenuNum)
                .orElseThrow(() -> new RuntimeException("메뉴가 존재하지 않음."));

        // 승인 처리
        menu.setApprovedByDev(true);
        roomMenuRepository.save(menu);

        // 필요 시 번역 테이블에 수동으로 insert 처리
        // translationRepository.save(new Translation(...));

        // 승인 후 다시 승인 대기 목록 페이지로 리다이렉트
        return "redirect:/roommenu/dev/translation";
    }

    /* 관리자가 속한 호텔의 객실 목록 */

    @GetMapping("/roommenu/roomList")
    public String getMyHotelRooms(Model model, Principal principal) { // keyword, Pageable은 현재 사용되지 않으므로 일단 제거 (필요 시 추가)

        String adminEmail = principal.getName();

        if (principal == null) {
            log.warn("인증되지 않은 사용자가 객실 목록 접근 시도.");
            return "redirect:/admin/login"; // 적절한 로그인 경로로 수정
        }

        log.info("관리자 '{}'의 호텔 객실 목록 조회를 요청합니다.", adminEmail);

        try {
            // Service 메소드 호출 수정 및 주입된 객체 이름 확인!
            // 만약 주입된 객체 이름이 roomMenuService이고 해당 클래스에 searchRoomList가 있다면 그대로 사용
            // 여기서는 HotelRoomService를 주입받았다고 가정하고 hotelRoomService 사용
            List<HotelRoomDTO> hotelRooms = roomMenuService.searchRoomList(adminEmail);

            model.addAttribute("hotelRooms", hotelRooms);
            // 로그 메시지 수정
            log.info("관리자 '{}'의 호텔 객실 {}건을 모델에 추가했습니다.", adminEmail, hotelRooms.size());

            // 템플릿 경로 확인
            return "roommenu/roomList"; // 이 경로에 실제 파일이 있는지 확인

        } catch (EntityNotFoundException e) {
            log.error("객실 목록 조회 중 오류 발생: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            return "error/adminError"; // 에러 페이지 경로 확인
        } catch (Exception e) {
            log.error("객실 목록 조회 중 예상치 못한 오류 발생", e);
            model.addAttribute("errorMessage", "객실 목록을 불러오는 중 오류가 발생했습니다.");
            return "error/adminError";
        }
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