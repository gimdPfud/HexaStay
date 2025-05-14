package com.sixthsense.hexastay.controller;

import com.sixthsense.hexastay.config.Security.CustomMemberDetails;
import com.sixthsense.hexastay.dto.*;
import com.sixthsense.hexastay.service.CouponService;
import com.sixthsense.hexastay.service.RoomMenuCartService;
import com.sixthsense.hexastay.service.RoomMenuService;
import com.sixthsense.hexastay.util.exception.SoldOutException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

/**************************************************
 * 클래스명 : RoomMenuCartController
 * 기능   : 룸서비스(장바구니)와 관련된 컨트롤러
 * - 룸서비스 메뉴 목록 조회 및 필터링 기능
 * - 장바구니 추가 및 조회 기능
 * 작성자 : 김윤겸
 * 작성일 : 2025-04-01
 * 수정일 : 2025-04-07
 * 입출력 변수 설계 : 김윤겸
 **************************************************/

@Controller
@RequiredArgsConstructor
@RequestMapping("/roommenu")
@Log4j2

public class RoomMenuCartController {

    private final RoomMenuCartService roomMenuCartService;
    private final RoomMenuService roomMenuService;
    private final CouponService couponService;

    /**************************************************
     * 메소드명 : RoomMenuCartItem
     * 장바구니 메뉴 아이템 추가
     * 기능: 사용자로부터 룸서비스 메뉴 아이템 정보(RoomMenuCartItemDTO)를 받아 유효성 검사를 수행한 후,
     * 로그인한 사용자의 장바구니에 해당 아이템을 추가합니다. 처리 결과를 ResponseEntity로 반환합니다.
     * 작성자 : 김윤겸
     * 등록일 : 04-08
     * 수정일 : 04-10
     **************************************************/

    @PostMapping("/orderpage/orderread")
    public ResponseEntity RoomMenuCartItem(
            @RequestBody @Valid RoomMenuCartItemDTO roomMenuCartItemDTO,
            BindingResult bindingResult,
            Principal principal) {
        log.info("장바구니 카트 컨트롤러 진입" + roomMenuCartItemDTO);

        if (bindingResult.hasErrors()) {
            log.info("장바구니 유효성검사 에러");
            log.info(bindingResult.getAllErrors());

            List<FieldError> fieldErrorList = bindingResult.getFieldErrors();
            StringBuilder stringBuilder = new StringBuilder();

            for (FieldError error : fieldErrorList) {
                // StringBuilder 객체에 에러 메시지를 담는다.
                stringBuilder.append(error.getDefaultMessage());
            }

            return new ResponseEntity<>(stringBuilder.toString(), HttpStatus.BAD_REQUEST);
        }

        if (principal == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String memberEmail = principal.getName();
        log.info("로그인한 사용자 이메일 : " + memberEmail);

        Long roomCartItemNum = null;

        try {
            roomCartItemNum = roomMenuCartService.RoomMenuCartInsert(memberEmail, roomMenuCartItemDTO);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(roomCartItemNum, HttpStatus.CREATED);

    }

    /**************************************************
     * 메소드명 : getRoomMenuCartItems
     * 장바구니 아이템 목록 조회
     * 기능: 로그인한 사용자의 장바구니에 담긴 아이템 목록과 사용 가능한 쿠폰 목록을 조회하여,
     * 모델에 담아 장바구니 목록 페이지('roommenu/cartlist')로 전달합니다. 페이지네이션을 지원합니다.
     * 작성자 : 김윤겸
     * 등록일 : 2025-04-08
     * 수정일 : 2025-04-10
     **************************************************/

    @GetMapping("/cartlist")
    public String getRoomMenuCartItems(
            Principal principal,
            Model model,
            Pageable pageable,
            RoomMenuDTO roomMenuDTO,
            @AuthenticationPrincipal CustomMemberDetails customMemberDetails) {
        log.info("장바구니 리스트 컨트롤러 진입");

        if (principal == null) {
            return "redirect:/member/login";
        }

        String email = principal.getName();
        log.info("로그인한 사용자: {}", email);

        Page<RoomMenuCartDetailDTO> cartDetailDTOList = roomMenuCartService.RoomMenuCartItemList(email, pageable);
        boolean isCartEmpty = cartDetailDTOList == null || cartDetailDTOList.isEmpty();

        List<CouponDTO> couponList = couponService.getCoupons(email);

        model.addAttribute("cartDetailDTOList", cartDetailDTOList);
        model.addAttribute("isCartEmpty", isCartEmpty);
        model.addAttribute("roomMenuDTO", roomMenuDTO);
        model.addAttribute("couponList", couponList); //

        return "roommenu/cartlist";
    }


    /**************************************************
     * 메소드명 : verifyRoomMenuCartItem
     * 장바구니 아이템 소유자 검증
     * 기능: 특정 장바구니 아이템 번호(roomMenuCartItemNum)와 사용자 이메일(email)을 받아,
     * 해당 아이템이 명시된 사용자의 장바구니에 속하는지를 검증하고 그 결과를 Boolean 형태로 반환합니다.
     * 작성자 : 김윤겸
     * 등록일 : 2025-04-08
     * 수정일 : 2025-04-10
     **************************************************/

    @GetMapping("/verify/{roomMenuCartItemNum}")
    public ResponseEntity<Boolean> verifyRoomMenuCartItem(@PathVariable Long roomMenuCartItemNum,
                                                          @RequestParam String email) {
        log.info("장바구니 유효성 검사 컨트롤러 진입");

        try {
            boolean isVerified = roomMenuCartService.verificationRoomMenuCartItem(roomMenuCartItemNum, email);
            return ResponseEntity.ok(isVerified);  // 200 OK 응답 반환
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);  // 예외 발생 시 400 Bad Request 반환
        }
    }

    /**************************************************
     * 메소드명 : modifyRoomMenuCartItemAmount
     * 장바구니 아이템 수량 변경
     * 기능: 특정 장바구니 아이템(roomMenuCartItemNum)의 수량(roomMenuCartItemAmount)을 변경합니다.
     * 수량은 1 이상이어야 하며, 요청한 사용자가 해당 아이템의 소유자인지 검증 후 처리합니다.
     * 작성자 : 김윤겸
     * 등록일 : 2025-04-08
     * 수정일 : 2025-04-10
     **************************************************/

    @PutMapping("/orderpage/read/{roomMenuCartItemNum}/{roomMenuCartItemAmount}")
    public ResponseEntity modifyRoomMenuCartItemAmount(
            @PathVariable("roomMenuCartItemNum") Long roomMenuCartItemNum,
            @PathVariable("roomMenuCartItemAmount") Integer roomMenuCartItemAmount,
            Principal principal) {
        log.info("장바구니 수량 변경 컨트롤러 진입" + roomMenuCartItemNum);

        if (roomMenuCartItemAmount <= 0) {
            return new ResponseEntity<String>("최소 1개 이상 담아주세요", HttpStatus.BAD_REQUEST);
        }

        // 로그인 정보 확인
        if (principal == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // 현재 카트가 나의 것인지 확인
        if (!roomMenuCartService.verificationRoomMenuCartItem(roomMenuCartItemNum, principal.getName())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);  // 권한이 없으면 접근 금지
        }

        // 카트 아이템의 수량을 변경
        roomMenuCartService.RoomMenuCartItemAmountUpdate(roomMenuCartItemNum, roomMenuCartItemAmount);

        return new ResponseEntity<>(HttpStatus.OK);  // 수량 변경이 성공적으로 완료되었으면 OK 반환

    }

    /**************************************************
     * 메소드명 : RoomMenuCartItemDeleteItem
     * 장바구니 아이템 삭제
     * 기능: 특정 장바구니 아이템(roomCartItemNum)을 삭제합니다.
     * 요청한 사용자가 해당 아이템의 소유자인지 검증 후 처리합니다.
     * 작성자 : 김윤겸
     * 등록일 : 2025-04-08
     * 수정일 : 2025-04-13
     **************************************************/

    @DeleteMapping("/roomMenuCartItemDelete/{roomCartItemNum}")
    public ResponseEntity<?> RoomMenuCartItemDeleteItem(@PathVariable Long roomCartItemNum, Principal principal) {
        log.info("장바구니 삭제 컨트롤러 진입 - 삭제할 번호: " + roomCartItemNum);

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        if (!roomMenuCartService.verificationRoomMenuCartItem(roomCartItemNum, principal.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("접근 권한 없음");
        }

        try {
            roomMenuCartService.RoomCartMenuCartItemDelete(roomCartItemNum);
            return ResponseEntity.ok("삭제 완료"); // 삭제 성공 후 OK
        } catch (EntityNotFoundException e) {
            log.error("삭제 중 예외 발생", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("아이템 삭제 중 오류 발생");
        }
    }

    /**************************************************
     * 메소드명 : applyCouponToCart
     * 장바구니에 쿠폰 적용 및 할인된 총액 계산
     * 기능: 특정 사용자(email)의 장바구니에 지정된 쿠폰(couponNum)을 적용하여,
     * 할인된 최종 결제 금액을 계산하여 반환합니다.
     * 작성자 : 김윤겸
     * 등록일 : 2025-04-24
     * 수정일 :
     **************************************************/

    @PostMapping("/apply-coupon")
    public ResponseEntity<?> applyCouponToCart(@RequestParam String email, @RequestParam Long couponNum) {
        log.info("장바구니 쿠폰 적용 컨트롤러 진입");

        int discountedTotal = roomMenuCartService.getTotalPriceWithCoupon(email, couponNum);
        return ResponseEntity.ok(discountedTotal);
    }

    @GetMapping("/api/products/{productId}/options")
    @ResponseBody // @RestController가 아니라면 필요
    public ResponseEntity<?> getAvailableOptions(@PathVariable("productId") Long productId) {
        try {
            log.info("(컨트롤러) 상품 ID {}에 대한 옵션 목록 조회 요청", productId);
            List<RoomMenuOptionDTO> options = roomMenuCartService.getAvailableOptionsForProduct(productId);
            return ResponseEntity.ok(options);
        } catch (EntityNotFoundException e) {
            log.warn("(컨트롤러) 옵션 목록 조회 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("(컨트롤러) 옵션 목록 조회 중 서버 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "옵션 목록 조회 중 오류가 발생했습니다."));
        }
    }

    // (추가) 장바구니 아이템 옵션 업데이트 API
    @PutMapping("/api/cart/items/{cartItemId}/options")
    @ResponseBody
    public ResponseEntity<?> updateCartOptions(
            @PathVariable("cartItemId") Long cartItemId,
            @RequestBody List<UpdateCartOptionRequestDTO> selectedOptionUpdates, // ★ DTO 타입 변경 ★
            Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "로그인이 필요합니다."));
        }
        String userEmail = principal.getName();
        log.info("(컨트롤러) 장바구니 아이템 ID {}의 옵션 업데이트 요청 - 사용자: {}", cartItemId, userEmail);

        try {
            roomMenuCartService.updateCartItemWithOptions(cartItemId, userEmail, selectedOptionUpdates);
            log.info("(컨트롤러) 장바구니 아이템 ID {} 옵션 업데이트 성공", cartItemId);
            return ResponseEntity.ok(Map.of("message", "옵션이 성공적으로 변경되었습니다."));
        } catch (EntityNotFoundException | IllegalArgumentException e) {
            log.warn("(컨트롤러) 옵션 업데이트 실패 (데이터 오류 또는 권한 없음): {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (SoldOutException e) {
            log.warn("(컨트롤러) 옵션 업데이트 실패 (재고 부족): {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage())); // 409 Conflict가 적절할 수 있음
        } catch (Exception e) {
            log.error("(컨트롤러) 옵션 업데이트 중 서버 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "옵션 변경 중 오류가 발생했습니다."));
        }
    }
}
