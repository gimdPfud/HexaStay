package com.sixthsense.hexastay.controller;

/**************************************************
 * 클래스명 : RoomMenuController
 * 기능   : 룸서비스 메뉴 관리와 관련된 요청을 처리하는 컨트롤러입니다.
 * 메뉴 목록 조회, 상세 보기, 등록, 수정, 삭제 및 다국어 지원 메뉴 승인 등의 기능을 제공합니다.
 * 작성자 : 김윤겸
 * 작성일 : 2025-04-01
 * 수정일 : 2025-05-09
 * 입출력 변수 설계 : 김윤겸
 **************************************************/

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.sixthsense.hexastay.dto.RoomMenuDTO;
import com.sixthsense.hexastay.entity.RoomMenu;
import com.sixthsense.hexastay.repository.RoomMenuOrderItemRepository;
import com.sixthsense.hexastay.repository.RoomMenuRepository;
import com.sixthsense.hexastay.service.RoomMenuCartService;
import com.sixthsense.hexastay.service.RoomMenuOptionService;
import com.sixthsense.hexastay.service.RoomMenuService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    private final RoomMenuRepository roomMenuRepository;
    private final RoomMenuOptionService roomMenuOptionService;
    private final RoomMenuOrderItemRepository roomMenuOrderItemRepository;


    /**************************************************
     * 메소드명 : roomMenuReadA
     * 룸서비스 메뉴 상세 정보 조회 (메소드명 구분용 'A')
     * 기능: 특정 룸서비스 메뉴 번호(num)와 현재 로케일(locale)을 기준으로 메뉴 상세 정보를 조회하여
     * 모델에 담아 'roommenu/read' 뷰로 전달합니다.
     * (주의: 동일 경로 @GetMapping("/roomMenu/read")를 사용하는 다른 메소드 존재로 인한 충돌 가능성 있음)
     * 작성자 : 김윤겸
     * 등록일 : 2025-04-07
     * 수정일 : -
     **************************************************/

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
     * 메소드명 : RoomServiceItemGet
     * 룸서비스 메뉴 등록 페이지 이동
     * 기능: 룸서비스 메뉴를 등록할 수 있는 'roommenu/insert' 페이지로 이동합니다.
     * 작성자 : 김윤겸
     * 등록일 : 2025-04-01
     * 수정일 : -
     **************************************************/

    @GetMapping("/roommenu/insert")
    public String RoomServiceItemGet(Principal principal) {
        log.info("등록페이지 get 컨트롤러 진입");
        return "roommenu/insert";
    }

    /**************************************************
     * 메소드명 : RoomServicePost
     * 룸서비스 메뉴 등록 처리
     * 기능: 사용자가 입력한 룸서비스 메뉴 정보(RoomMenuDTO)를 받아와 서비스를 통해 등록 처리하고,
     * 성공 시 메뉴 목록 페이지로 리다이렉트합니다. 로그인한 사용자 정보(Principal)를 참조합니다.
     * 작성자 : 김윤겸
     * 등록일 : 2025-04-01
     * 수정일 : -
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

        roomMenuService.insert(roomMenuDTO);

        return "redirect:/roommenu/list";
    }

    /**************************************************
     * 메소드명 : RoomMenuList
     * 룸서비스 메뉴 목록 조회
     * 기능: 검색 조건(type, keyword, category), 페이지네이션 정보, 현재 로케일(locale)을 바탕으로
     * 룸서비스 메뉴 목록(관리자/일반 공용)을 조회하여 모델에 담아 'roommenu/list' 뷰로 전달합니다.
     * 작성자 : 김윤겸
     * 등록일 : 2025-04-01
     * 수정일 : 2025-04-07
     **************************************************/

    @GetMapping("/roommenu/list")
    public String RoomMenuList(@PageableDefault(size = 8) Pageable pageable,
                               @RequestParam(value="type", required = false, defaultValue = "") String type,
                               @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
                               @RequestParam(value = "category", required = false) String category,
                               Model model, Locale locale) {
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

    /**************************************************
     * 메소드명 : roomMenuOrderRead
     * 주문 페이지 내 메뉴 상세 정보 조회
     * 기능: 주문 페이지에서 특정 룸서비스 메뉴 번호(num)와 현재 로케일(locale)을 기준으로
     * 메뉴 상세 정보(장바구니 서비스 사용)를 조회하여 모델에 담아 'roommenu/orderpage/orderread'
     * 뷰로 전달합니다. (주의: 동일 경로 @GetMapping("/roommenu/orderpage/orderread")를 사용하는
     * 다른 메소드 존재로 인한 충돌 가능성 있음)
     * 작성자 : 김윤겸
     * 등록일 : 2025-04-08
     * 수정일 : 2025-04-18 [다국어 추가]
     **************************************************/

    @GetMapping("/roommenu/orderpage/orderread")
    public String roomMenuOrderRead(@RequestParam Long num, Model model, Locale locale) {
        log.info("상세보기 orderpage 컨트롤러 진입: " + num);

        RoomMenuDTO roomMenuDTO = roomMenuCartService.read(num, locale, model);
        model.addAttribute("roomMenuDTO", roomMenuDTO);
        log.info("모델로 받은 dto: " + roomMenuDTO);

        return "roommenu/orderpage/orderread";
    }

    /**************************************************
     * 메소드명 : roomMenuRead
     * 룸서비스 메뉴 상세 정보 조회 (장바구니 서비스 이용)
     * 기능: 특정 룸서비스 메뉴 번호(num)와 현재 로케일(locale)을 기준으로 장바구니 서비스를 통해
     * 메뉴 상세 정보를 조회하여 모델에 담아 'roommenu/read' 뷰로 전달합니다.
     * (주의: 동일 경로 @GetMapping("/roomMenu/read")를 사용하는 다른 메소드 존재로 인한 충돌 가능성 있음)
     * 작성자 : 김윤겸
     * 등록일 : 2025-04-02
     * 수정일 : 2025-04-19
     **************************************************/

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
     * 메소드명 : roomMenuModifyGet
     * 룸서비스 메뉴 수정 페이지 이동
     * 기능: 수정할 룸서비스 메뉴 번호(num)와 현재 로케일(locale)을 받아 해당 메뉴 정보를 조회하여
     * 모델에 담고, 'roommenu/modify' 수정 페이지로 이동합니다.
     * 작성자 : 김윤겸
     * 등록일 : 2025-04-02
     * 수정일 : 2025-04-27
     **************************************************/

    @GetMapping("/roommenu/modify")
    public String roomMenuModifyGet(Long num, Model model, Locale locale) {
        log.info("Get 수정 컨트롤러 진입: " + num);

        try {
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
     * 메소드명 : roomMenuModifyPost
     * 룸서비스 메뉴 수정 처리
     * 기능: 사용자가 수정한 룸서비스 메뉴 정보(RoomMenuDTO)를 받아 서비스를 통해 업데이트하고,
     * 성공 시 해당 메뉴의 상세 보기 페이지로 리다이렉트합니다. 실패 시 목록 페이지로 리다이렉트하며
     * 메시지를 전달합니다.
     * 작성자 : 김윤겸
     * 등록일 : 2025-04-02
     * 수정일 : 2025-04-09
     **************************************************/

    @PostMapping("/roommenu/modify")
    public String roomMenuModifyPost(RoomMenuDTO roomMenuDTO, Principal principal, RedirectAttributes redirectAttributes) {

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
     * 메소드명 : roomMenuDelete
     * 룸서비스 메뉴 삭제 처리
     * 기능: 삭제할 룸서비스 메뉴 번호(num)를 받아, 해당 메뉴에 연결된 옵션이나 주문 이력이 없는 경우
     * 삭제 처리합니다. 처리 결과에 따라 메시지와 함께 메뉴 목록 페이지로 리다이렉트합니다.
     * 작성자 : 김윤겸
     * 등록일 : 2025-04-02
     * 수정일 : 2025-05-03 [주문이력 삭제 수정]
     **************************************************/

    @PostMapping("/roommenu/delete")
    public String roomMenuDelete(Long num, Principal principal, RedirectAttributes redirectAttributes){
        log.info("삭제 컨트롤러 진입" + num);

        // 옵션 존재 여부 확인
        if (roomMenuOptionService.hasOption(num)) {
            redirectAttributes.addFlashAttribute("errorMessage", "옵션이 존재합니다. 옵션을 먼저 삭제해주세요.");
            return "redirect:/roommenu/list";
        }

        // 2. 주문 이력 존재 여부 확인
        boolean isOrdered = roomMenuOrderItemRepository.existsByRoomMenuRoomMenuNum(num);
        if (isOrdered) {
            redirectAttributes.addFlashAttribute("errorMessage", "해당 메뉴는 이미 주문된 이력이 있어 삭제할 수 없습니다.");
            return "redirect:/roommenu/list";
        }

        String memberName = principal.getName();  // 로그인한 사용자의 이름 (또는 ID)

        roomMenuService.delete(num);
        log.info("삭제한 사용자" + memberName);

        return "redirect:/roommenu/list";

    }


    /**************************************************
     * 메소드명 : roomMenuOrderpageRead (by email)
     * (사용자별) 주문 페이지 상품 정보 조회
     * 기능: 사용자 이메일(email)을 기준으로 장바구니 서비스에서 상품 정보를 조회하여 모델에 담고,
     * 'roommenu/orderpage/orderread' 뷰로 전달합니다. 사용자가 로그인하지 않았거나 상품이 없을 경우 리다이렉트합니다.
     * (주의: 동일 경로 @GetMapping("/roommenu/orderpage/orderread")를 사용하는 다른 메소드 존재로 인한 충돌 가능성 있음)
     * 작성자 : 김윤겸
     * 등록일 : 2025-04-10
     * 수정일 : -
     **************************************************/

    @GetMapping("/roommenu/orderpage/read")
    public String roomMenuOrderpageRead(String email, Model model ,RedirectAttributes redirectAttributes) {
        log.info("상품정보 페이지" + email);

        if (email == null) {
            return "redirect:member/login";
        }

        try {
            RoomMenuDTO roomMenuDTO =
                    roomMenuCartService.RoomMenuCartRead(email);

            model.addAttribute("roomMenuDTO", roomMenuDTO);

            return "roommenu/orderpage/orderread";

        } catch (EntityNotFoundException e) {

            redirectAttributes.addFlashAttribute("msg", "존재하지 않는 상품입니다.");
            return "redirect:/roommenu/orderpage";
        }
    }

    /**************************************************
     * 메소드명 : orderList
     * 룸서비스 주문 페이지 메뉴 목록 조회 (사용자용)
     * 기능: 사용자 주문 페이지에서 검색 조건(type, keyword, category), 페이지네이션 정보,
     * 현재 로케일(locale)을 바탕으로 사용자용 룸서비스 메뉴 목록을 조회합니다.
     * 또한, 로그인한 사용자의 장바구니 총 아이템 개수를 조회하여 모델에 담아 'roommenu/orderpage' 뷰로 전달합니다.
     * 작성자 : 김윤겸
     * 등록일 : 2025-04-07
     * 수정일 : 2025-04-18 [무한스크롤 오류 수정]
     **************************************************/

    @GetMapping("/roommenu/orderpage")
    public String orderList(@PageableDefault(page = 0, size = 100)  Pageable pageable,
                            @RequestParam(value = "type", defaultValue = "") String type,
                            @RequestParam(value = "keyword", defaultValue = "") String keyword,
                            @RequestParam(value = "category", defaultValue = "") String category,
                            Principal principal,
                            Locale locale,
                            Model model) throws JsonProcessingException { // 무한스크롤 비슷하게 느낌을 내기 위해서 size를 100으로 조정
        // size = Integer.MAX_VALUE 으로 다 불러올 수 있지만 데이터 많으면 오류생기니까 그냥 맵두자.
        log.info("주문페이지 컨트롤러 리스트 진입");
        log.info("로그인한 사용자: " + principal.getName());

        String email = principal.getName(); // 로그인한 사용자의 이메일
        Integer totalCartItemCount = roomMenuCartService.getTotalCartItemCount(email);

        String lang = locale.getLanguage(); // ex) "ko", "en"

        Page<RoomMenuDTO> roomMenuList =
                roomMenuService.RoomMenuList(pageable, type, keyword, category, locale, true); // <-- 유저용

        Map<String, Integer> pageInfo = Pagination(roomMenuList);

        ObjectMapper objectMapper = new ObjectMapper();
        SimpleFilterProvider filters = new SimpleFilterProvider()
                .addFilter("roomMenuFilter",
                        SimpleBeanPropertyFilter.serializeAllExcept(
                                "createDate", "modifyDate", "supportsMultilang", "approvedByDev", "room", "hotelRoomNum"
                        ));
        String roomMenuListJson = objectMapper.writer(filters)
                .writeValueAsString(roomMenuList.getContent());

        model.addAttribute("list", roomMenuList);
        model.addAttribute("listJson", roomMenuListJson); // JS에서 사용
        model.addAttribute("type", type);
        model.addAttribute("keyword", keyword);
        model.addAttribute("category", category);
        model.addAttribute("totalCartItemCount", totalCartItemCount);
        model.addAllAttributes(pageInfo);



        return "roommenu/orderpage";

    }

    /**************************************************
     * 메소드명 : approvalList
     * 개발자 승인 대기 다국어 메뉴 조회
     * 기능: 개발자 승인이 필요한 다국어 지원 룸서비스 메뉴 목록을 페이지네이션하여 조회하고,
     * 이를 모델에 담아 'roommenu/dev/translation' 뷰로 전달합니다.
     * 작성자 : 김윤겸
     * 등록일 : 2025-04-18
     * 수정일 : -
     **************************************************/

    @GetMapping("/roommenu/dev/translation")
    public String approvalList(Model model, Pageable pageable) {
        log.info("다국어 승인 컨트롤러 진입");
        Page<RoomMenu> pendingMenus = roomMenuRepository.findByApprovedByDevFalseAndSupportsMultilangTrue(pageable);
        model.addAttribute("pendingMenus", pendingMenus);
        return "roommenu/dev/translation"; // HTML 파일 이름
    }

    /**************************************************
     * 메소드명 : approveMenu
     * 다국어 룸서비스 메뉴 개발자 승인 처리
     * 기능: 특정 룸서비스 메뉴 번호(roomMenuNum)를 받아 해당 메뉴를 개발자 승인(다국어 지원 관련) 처리하고,
     * 데이터베이스에 저장한 후 승인 대기 목록 페이지로 리다이렉트합니다.
     * 작성자 : 김윤겸
     * 등록일 : 2025-04-18
     * 수정일 : -
     **************************************************/

    @PostMapping("/roommenu/dev/translation/{roomMenuNum}")
    public String approveMenu(@PathVariable Long roomMenuNum) {
        log.info("다국어 룸서비스 메뉴 개발자 승인 처리 컨트롤러 진입");

        // 메뉴 검색
        RoomMenu menu = roomMenuRepository.findById(roomMenuNum)
                .orElseThrow(() -> new RuntimeException("메뉴가 존재하지 않음."));

        // 승인 처리
        menu.setApprovedByDev(true);
        roomMenuRepository.save(menu);

        // 승인 후 다시 승인 대기 목록 페이지로 리다이렉트
        return "redirect:/roommenu/dev/translation";
    }

}
