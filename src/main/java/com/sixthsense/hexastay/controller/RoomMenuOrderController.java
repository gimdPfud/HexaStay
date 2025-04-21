package com.sixthsense.hexastay.controller;

/* 클래스명 : RoomMenuController
 * 기능 : 룸서비스(메뉴)와 관련된 컨트롤러
 *        룸서비스의 메뉴를 관리하는 다양한 페이지를 처리하는 컨트롤러.
 *        메뉴 목록 조회, 상세보기, 등록, 수정, 삭제 등의 기능을 담당
 * 작성자 : 김윤겸
 * 작성일 : 2025-04-09
 * 수정일 : 2025-00-00 입출력변수설계 : 김윤겸 */

import com.sixthsense.hexastay.dto.RoomMenuOrderDTO;
import com.sixthsense.hexastay.entity.Member;
import com.sixthsense.hexastay.entity.RoomMenuOrder;
import com.sixthsense.hexastay.enums.AdminRole;
import com.sixthsense.hexastay.repository.MemberRepository;
import com.sixthsense.hexastay.repository.RoomMenuOrderRepository;
import com.sixthsense.hexastay.service.RoomMenuOrderService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.AccessDeniedException;

import java.io.IOException;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.sixthsense.hexastay.util.PaginationUtil.Pagination;

@Controller
@RequiredArgsConstructor
@Log4j2

public class RoomMenuOrderController {

    private final RoomMenuOrderService roomMenuOrderService;
    private final RoomMenuOrderRepository roomMenuOrderRepository;
    private final MemberRepository memberRepository;

    /***************************************************
     *
     * 클래스명   : roomMenuOrderPost
     * 기능      : 룸서비스 메뉴 주문을 처리하는 POST 컨트롤러 메소드
     *            - 클라이언트로부터 전달받은 주문 DTO의 유효성 검증
     *            - 로그인 여부 확인 후, 주문 서비스 호출
     *            - 주문 수량이 재고보다 많을 경우 예외 처리
     *            - 처리 결과를 ResponseEntity 형태로 반환
     * 작성자    : 김윤겸
     * 작성일    : 2025-04-11
     * 수정일    : -
     *
     ****************************************************/

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

        // 현재 로그인한 사용자의 이메일 가져오기
        String email = principal.getName();

        Long roomMenuOrderNum = null;

        try {
            // 주문 서비스 호출 → 주문 생성 및 저장
            roomMenuOrderNum = roomMenuOrderService.roomMenuOrderInsert(roomMenuOrderDTO, email);

            // 관리자의 알람을 위해서

            roomMenuOrderService.RoomMenuSendOrderAlert(roomMenuOrderDTO);

            log.info(String.format("주문이 완료됨 - DTO: %s, Email: %s", roomMenuOrderDTO, email));


        } catch (IllegalStateException e) {
            // 재고 부족 등의 IllegalStateException 발생 시 BAD_REQUEST로 에러 메시지 반환
            log.info("재고가 부족합니다.");
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        // 주문 생성 성공 시, 주문 번호 반환 (HTTP 200 OK)
        return new ResponseEntity<Long>(roomMenuOrderNum, HttpStatus.OK);
    }


    /***************************************************
     *
     * 클래스명   : createOrderFromCart
     * 기능      : 장바구니를 기반으로 룸서비스 메뉴 주문을 처리하는 POST 컨트롤러 메소드
     * - 로그인한 사용자의 장바구니 정보를 이용하여 주문 생성
     * - requestMessage 파라미터를 통해 추가적인 요청 사항을 전달받음
     * - 주문 서비스 호출 후 생성된 주문 번호를 ResponseEntity 형태로 반환
     * - 주문 실패 시 에러 메시지를 담아 HTTP 상태 코드를 반환
     * 작성자    : 김윤겸
     * 작성일    : 2025-04-15
     * 수정일    : -
     *
     ****************************************************/

    @PostMapping("/roommenu/cart")
    public ResponseEntity<?> createOrderFromCart(Principal principal, String requestMessage) {
        log.info("POST /order/cart 컨트롤러 진입");
        log.info("로그인한 사용자 : " + principal.getName());

        if (principal == null) {
            log.info("로그인이 필요합니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        String email = principal.getName();

        try {
            Long orderNum = roomMenuOrderService.roomMenuOrderInsertFromCart(email, requestMessage);
            log.info("주문 생성 완료 - 주문번호: {}", orderNum);
            return ResponseEntity.ok(orderNum);
        } catch (IllegalStateException | EntityNotFoundException e) {
            log.error("주문 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            log.error("서버 에러: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("주문 처리 중 오류 발생");
        }
    }

    /***************************************************
     *
     * 클래스명   : getOrderList
     * 기능      : 현재 로그인한 사용자의 룸서비스 주문 목록을 조회하여 화면에 표시하는 GET 컨트롤러 메소드
     * - 로그인 여부 확인 후, 주문 서비스 호출
     * - 조회된 주문 목록을 Model 객체에 담아 뷰로 전달
     * - 로그인되지 않은 경우 로그인 페이지로 리다이렉트
     * 작성자    : 김윤겸
     * 작성일    : 2025-04-15
     * 수정일    : -
     *
     ****************************************************/

    @GetMapping("/roommenu/orderList")
    public String getOrderList(Principal principal, Model model) {
        log.info("주문 리스트 페이지 컨트롤러 진입");
        if (principal == null) {
            return "redirect:/member/login"; // 로그인 안됐으면 로그인 페이지로
        }

        String email = principal.getName();

        List<RoomMenuOrderDTO> orderList = roomMenuOrderService.getOrderListByEmail(email);
        model.addAttribute("orderList", orderList);

        return "roommenu/orderList"; // templates/roommenu/orderList.html
    }

    /***************************************************
     *
     * 클래스명   : cancelOrder
     * 기능      : 특정 주문 번호에 해당하는 룸서비스 주문을 취소하는 POST 컨트롤러 메소드
     * - 로그인 여부 확인 및 주문 취소 권한 검증
     * - 주문 서비스의 주문 취소 기능 호출
     * - 처리 결과를 ResponseEntity 형태로 반환 (성공 시 "주문이 성공적으로 취소되었습니다.", 실패 시 에러 메시지)
     * 작성자    : 김윤겸
     * 작성일    : 2025-04-15
     * 수정일    : -
     *
     ****************************************************/

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

    /***************************************************
     *
     * 클래스명   : CashOrderPageGet
     * 기능      : 현금 결제 페이지를 보여주는 GET 컨트롤러 메소드
     * - 별도의 데이터 조회 없이 현금 결제 관련 뷰를 반환
     * 작성자    : 김윤겸
     * 작성일    : 2025-04-15
     * 수정일    : -
     *
     ****************************************************/

    @GetMapping("/roommenu/cashOrder")
    public String CashOrderPageGet() {
        log.info("현금 결제 컨트롤러 진입");
//            // 룸방번호
//            model.addAttribute("roomName", roomName);
        return "roommenu/cashOrder";  // templates/cashOrder.html로 연결
    }

    /***************************************************
     *
     * 클래스명   : viewAllOrders
     * 기능      : 관리자 권한으로 모든 룸서비스 주문 목록을 조회하여 화면에 표시하는 GET 컨트롤러 메소드
     * - 주문 서비스의 모든 주문 조회 기능 호출
     * - 조회된 주문 목록을 Model 객체에 담아 관리자 주문 목록 뷰로 전달
     * 작성자    : 김윤겸
     * 작성일    : 2025-04-15
     * 수정일    : -
     *
     ****************************************************/

    @GetMapping("/roommenu/adminOrderList")
    public String viewAllOrders(Model model, Principal principal) {
        log.info("로그인한 사용자 : " + principal.getName());

        Member member = memberRepository.findByMemberEmail(principal.getName());

        // String → AdminRole enum으로 변환
        boolean isAdminRole = false;
        try {
            AdminRole userRole = AdminRole.valueOf(member.getMemberRole().toUpperCase());
            isAdminRole = Arrays.stream(AdminRole.values())
                    .anyMatch(role -> role == userRole);
        } catch (IllegalArgumentException e) {
            isAdminRole = false; // enum에 없는 문자열일 경우 예외 발생함
        }

        List<RoomMenuOrderDTO> orders = roomMenuOrderService.getAllOrdersForAdmin();
        model.addAttribute("orders", orders);

        return "roommenu/adminOrderList";
    }

    /***************************************************
     *
     * 클래스명   : completeOrders
     * 기능      : 전달받은 주문 ID 목록을 기준으로 주문 완료 처리 (임시 기능)
     * - 요청 Body에 JSON 형태로 주문 ID 목록을 받음
     * - 각 주문 ID에 대해 주문 삭제 (or 상태 변경) 로직 수행
     * - 처리 완료 메시지를 ResponseEntity 형태로 반환
     * - 오류 발생 시 HTTP 상태 코드와 에러 메시지 반환
     * 작성자    : 김윤겸
     * 작성일    : 2025-04-17
     * 수정일    : -
     *
     ****************************************************/

    // 임시임.. 체크여부에 따라서 삭제 기능
    @PostMapping("/roommenu/complete-orders")
    public ResponseEntity<?> completeOrders(@RequestBody List<Long> orderIds) {
        try {
            for (Long orderId : orderIds) {
                roomMenuOrderRepository.deleteById(orderId);  // 삭제 or 상태변경 로직
            }
            return ResponseEntity.ok("처리 완료");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("에러: " + e.getMessage());
        }
    }
}