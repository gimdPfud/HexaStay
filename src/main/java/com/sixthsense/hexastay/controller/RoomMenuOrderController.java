package com.sixthsense.hexastay.controller;

/**************************************************
 * 클래스명 : RoomMenuOrderController
 * 기능   : 룸서비스 주문과 관련된 요청을 처리하는 컨트롤러입니다.
 * 주문 생성(개별/장바구니), 주문 목록 조회(사용자/관리자), 주문 취소 등의 기능을 담당합니다.
 * 작성자 : 김윤겸
 * 작성일 : 2025-04-11
 * 수정일 : 2025-05-09
 * 입출력 변수 설계 : 김윤겸
 **************************************************/

import com.sixthsense.hexastay.dto.RoomMenuOrderDTO;
import com.sixthsense.hexastay.entity.Member;
import com.sixthsense.hexastay.entity.RoomMenuOrder;
import com.sixthsense.hexastay.enums.AdminRole;
import com.sixthsense.hexastay.repository.HotelRoomRepository;
import com.sixthsense.hexastay.repository.MemberRepository;
import com.sixthsense.hexastay.repository.RoomMenuOrderRepository;
import com.sixthsense.hexastay.service.HotelRoomService;
import com.sixthsense.hexastay.service.RoomMenuOrderService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.AccessDeniedException;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2

public class RoomMenuOrderController {

    private final RoomMenuOrderService roomMenuOrderService;
    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    /**************************************************
     * 메소드명 : roomMenuOrderPost
     * 룸서비스 개별 메뉴 주문 처리
     * 기능: 클라이언트로부터 전달받은 개별 룸서비스 주문 정보(RoomMenuOrderDTO)의 유효성을 검증하고,
     * 로그인한 사용자의 이름으로 주문을 생성합니다. 재고 부족 등 예외 발생 시 적절한 HTTP 상태 코드로 응답하며,
     * 성공 시 생성된 주문 번호를 반환합니다.
     * 작성자 : 김윤겸
     * 등록일 : 2025-04-11
     * 수정일 : -
     **************************************************/

    @PostMapping("/order")
    public ResponseEntity roomMenuOrderPost(@RequestBody @Valid RoomMenuOrderDTO roomMenuOrderDTO,
                                            BindingResult bindingResult, Principal principal, HttpServletRequest request) {
        log.info("주문하기 Post 컨트롤러 진입" + roomMenuOrderDTO);

        // DTO 유효성 검사 결과에 에러가 존재할 경우
        if (bindingResult.hasErrors()) {
            log.info("유효성검사 실패 - 필드 에러 발생");

            // 에러 메시지를 누적해서 응답하기 위한 StringBuilder
            StringBuilder sb = new StringBuilder();

            // 발생한 모든 필드 에러 가져오기
            List<FieldError> fieldErrorList = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrorList) {
                sb.append(fieldError.getDefaultMessage());  // 각 에러 메시지 추가
            }

            log.info(sb.toString());

            // BAD_REQUEST(400) 상태로 클라이언트에 에러 메시지 반환
            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);
        }

        // 사용자가 로그인하지 않은 경우
        if (principal == null) {
            log.info("로그인이 활성화 상태가 아닙니다. 로그인을 해 주십시오.");

            // 이전 페이지 주소 확인
            log.info("이전페이지 주소: " + request.getHeader("referer"));

            // UNAUTHORIZED(401) 상태로 이전 페이지 주소 반환
            return new ResponseEntity<String>(request.getHeader("referer"), HttpStatus.UNAUTHORIZED);
        }

        String email = principal.getName();
        Long roomMenuOrderNum = null;

        try {
            log.info("주문 insert 시작");
            roomMenuOrderNum = roomMenuOrderService.roomMenuOrderInsert(roomMenuOrderDTO, email);
            log.info("2️⃣ 주문 insert 완료 - 주문번호: {}", roomMenuOrderNum);

        } catch (IllegalStateException e) {
            // 재고 부족 등의 IllegalStateException 발생 시 BAD_REQUEST로 에러 메시지 반환
            log.info("재고가 부족합니다.");
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<Long>(roomMenuOrderNum, HttpStatus.OK);
    }


    /**************************************************
     * 메소드명 : createOrderFromCart
     * 장바구니 기반 룸서비스 주문 처리
     * 기능: 로그인한 사용자의 장바구니 정보를 기반으로 룸서비스 주문을 생성합니다.
     * 추가 요청 메시지, 쿠폰 정보, 할인된 총액 등을 파라미터로 받아 처리하며,
     * 주문 생성 후 알림 전송 로직을 호출합니다. 성공 시 생성된 주문 번호를, 실패 시 오류 메시지를 반환합니다.
     * 작성자 : 김윤겸
     * 등록일 : 2025-04-15
     * 수정일 : 2025-05-01
     **************************************************/

    @PostMapping("/roommenu/cart")
    public ResponseEntity<?> createOrderFromCart(Principal principal, String requestMessage, RoomMenuOrderDTO roomMenuOrderDTO,
                                                 Long couponNum, Integer discountedTotalPrice, Pageable pageable, HttpSession session) {
        log.info("POST /order/cart 컨트롤러 진입");
        log.info("로그인한 사용자 : " + principal.getName());

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        String email = principal.getName();
        String password = (String) session.getAttribute("roomPassword");

        try {
            RoomMenuOrder order = roomMenuOrderService.roomMenuOrderInsertFromCart(email, requestMessage, couponNum, discountedTotalPrice, pageable, password);
            log.info("주문 생성 완료 - 주문번호: {}", order.getRoomMenuOrderNum());

            roomMenuOrderService.RoomMenuSendOrderAlert(roomMenuOrderDTO, order, pageable); // orderDto가 어떻게 채워지는지 확인 필요
            log.info("3️⃣ 알람 전송 완료");

            return ResponseEntity.ok(order.getRoomMenuOrderNum());

        } catch (IllegalStateException | EntityNotFoundException e) {
            log.error("주문 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            // 전체 스택 트레이스를 로깅하는 것이 디버깅에 더 좋습니다.
            log.error("서버 에러 발생", e); // e.getMessage() 대신 전체 예외 로깅
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("주문 처리 중 오류 발생");
        }
    }

    /**************************************************
     * 메소드명 : getOrderList
     * 사용자 룸서비스 주문 목록 조회
     * 기능: 현재 로그인한 사용자의 룸서비스 주문 목록을 페이지네이션하여 조회합니다.
     * 조회된 주문 목록과 페이징 관련 정보를 모델에 담아 'roommenu/orderList' 뷰로 전달합니다.
     * 작성자 : 김윤겸
     * 등록일 : 2025-04-16
     * 수정일 : 2025-04-29
     **************************************************/

    @GetMapping("/roommenu/orderList")
    public String getOrderList(Principal principal,
                               Model model,
                               @RequestParam(name = "page", defaultValue = "0") int page) {
        log.info("주문 리스트 페이지 컨트롤러 진입");
        if (principal == null) {
            log.warn("비로그인 사용자가 주문 리스트 접근 시도");
            return "redirect:/member/login"; // 로그인 안 되었으면 로그인 페이지로 이동
        }

        String email = principal.getName();
        log.info("로그인한 사용자: {}", email);

        // 한 페이지당 5건씩, 등록일 기준 내림차순 정렬 (페이지 번호는 0부터 시작)
        // Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "regDate")); // 기존 10건
        Pageable pageable = PageRequest.of(page, 5, Sort.by(Sort.Direction.DESC, "regDate")); // 5건으로 수정하신 듯

        Page<RoomMenuOrderDTO> orderPage = roomMenuOrderService.getOrderListByEmail(email, pageable);

        // 서비스로부터 받은 DTO 리스트
        List<RoomMenuOrderDTO> orderListForModel = orderPage.getContent();

        // Model에 데이터 추가
        model.addAttribute("orderList", orderListForModel); // DTO 리스트를 "orderList" 이름으로 Model에 담습니다.

        // 페이징 네비게이션에 사용할 값들 (orderPage 객체에서 가져옴)
        int totalPages = orderPage.getTotalPages();
        model.addAttribute("totalPages", totalPages);

        // 현재 페이지(사용자에게는 1부터 보이도록 변환)
        int currentPage = page + 1;
        model.addAttribute("currentPage", currentPage);

        // 화면에 표시할 페이지 번호 범위를 계산 (예: 현재 페이지 기준 앞뒤 2페이지)
        int startPage = Math.max(1, currentPage - 2);
        int endPage = Math.min(totalPages, currentPage + 2);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        // 이전, 다음 페이지 처리 (단순 계산)
        int prevPage = currentPage > 1 ? currentPage - 1 : 1;
        int nextPage = currentPage < totalPages ? currentPage + 1 : totalPages;
        model.addAttribute("prevPage", prevPage);
        model.addAttribute("nextPage", nextPage);

        log.info("Model에 orderList 등 데이터 추가 완료. 템플릿 렌더링 시작: roommenu/orderList.html");
        return "roommenu/orderList"; // templates/roommenu/orderList.html 템플릿 사용
    }

    /**************************************************
     * 메소드명 : cancelOrder
     * 룸서비스 주문 취소 처리
     * 기능: 특정 주문 번호(orderNum)에 해당하는 룸서비스 주문을 취소합니다.
     * 로그인한 사용자가 해당 주문을 취소할 권한이 있는지 확인 후, 주문 취소 서비스를 호출합니다.
     * 처리 결과를 ResponseEntity로 반환합니다.
     * 작성자 : 김윤겸
     * 등록일 : 2025-04-16
     * 수정일 : -
     **************************************************/

    // 주문 취소
    @PostMapping("/roommenu/deleteOrder")
    public ResponseEntity<String> cancelOrder(@RequestParam Long orderNum, Principal principal) {
        log.info("주문 취소 컨트롤러 post 진입");

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        try {
            String email = principal.getName();
            roomMenuOrderService.cancelRoomMenuOrder(orderNum, email);
            return ResponseEntity.ok("주문이 성공적으로 취소되었습니다.");
        } catch (IllegalStateException | EntityNotFoundException | AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("주문 취소 중 오류 발생");
        }
    }

    /**************************************************
     * 메소드명 : viewAllOrders
     * 관리자용 전체 룸서비스 주문 목록 조회
     * 기능: 관리자 권한으로 모든 룸서비스 주문 목록을 페이지네이션하여 조회합니다.
     * 로그인한 사용자가 관리자 역할을 가졌는지 확인 후, 조회된 주문 목록과 페이징 정보를
     * 모델에 담아 'roommenu/adminOrderList' 뷰로 전달합니다.
     * 작성자 : 김윤겸
     * 등록일 : 2025-04-23
     * 수정일 : -
     **************************************************/

    @GetMapping("/roommenu/adminOrderList")
    public String viewAllOrders(
            @RequestParam(name = "page", defaultValue = "0") int page,
            Model model,
            Principal principal) {
        log.info("관리자용 룸 메뉴 오더 컨트롤러 진입");
        log.info("로그인한 사용자 : " + principal.getName());
        Member member = memberRepository.findByMemberEmail(principal.getName());

        // String → AdminRole enum으로 변환
        boolean isAdminRole = false;
        try {
            AdminRole userRole = AdminRole.valueOf(member.getMemberRole().toUpperCase());
            isAdminRole = Arrays.stream(AdminRole.values())
                    .anyMatch(role -> role == userRole);
        } catch (IllegalArgumentException e) {
            isAdminRole = false;
        }

        // 한 페이지당 10건, 최신순 정렬 기준. 페이지 번호 0부터 시작.
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "regDate"));
        Page<RoomMenuOrderDTO> orderPage = roomMenuOrderService.getAllOrdersForAdmin(pageable);
        List<RoomMenuOrderDTO> orders = orderPage.getContent();
        model.addAttribute("orders", orders);

        // 페이지 네비게이션용 값들을 계산 (화면에는 1부터 보이지만 실제 번호는 0부터 시작)
        int totalPages = orderPage.getTotalPages();
        int currentPage = page + 1;
        int startPage = Math.max(1, currentPage - 2);
        int endPage = Math.min(totalPages, currentPage + 2);
        int prevPage = currentPage > 1 ? currentPage - 1 : 1;
        int nextPage = currentPage < totalPages ? currentPage + 1 : totalPages;

        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("prevPage", prevPage);
        model.addAttribute("nextPage", nextPage);

        return "roommenu/adminOrderList";
    }
}