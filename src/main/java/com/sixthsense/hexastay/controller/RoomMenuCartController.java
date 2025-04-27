package com.sixthsense.hexastay.controller;

import com.sixthsense.hexastay.config.Security.CustomMemberDetails;
import com.sixthsense.hexastay.dto.*;
import com.sixthsense.hexastay.service.CouponService;
import com.sixthsense.hexastay.service.RoomMenuCartService;
import com.sixthsense.hexastay.service.RoomMenuService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

import static com.sixthsense.hexastay.util.PaginationUtil.Pagination;

/**************************************************
 * 클래스명 : RoomMenuCartController
 * 기능   : 룸서비스(장바구니)와 관련된 컨트롤러
 *        - 룸서비스 메뉴 목록 조회 및 필터링 기능
 *        - 장바구니 추가 및 조회 기능
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

    /***************************************************
     *
     * 메소드명   : RoomMenuCartItem
     * 기능      : 장바구니에 메뉴 아이템을 추가
     * 작성자    : 김윤겸
     * 작성일    : 2025-04-08
     * 수정일    : 2025-04-10
     ****************************************************/

    @PostMapping("/orderpage/orderread")
    public ResponseEntity RoomMenuCartItem(
            @RequestBody @Valid RoomMenuCartItemDTO roomMenuCartItemDTO,
            BindingResult bindingResult,
            Principal principal) {
        log.info("장바구니 카트 컨트롤러 진입" + roomMenuCartItemDTO);
        log.info(" 받은 DTO: {}", roomMenuCartItemDTO);
        log.info(" 수량: {}", roomMenuCartItemDTO.getRoomMenuCartItemAmount());//이게 널로 나오지 말아야함 OK

        // 유효성 검사
        if (bindingResult.hasErrors()) {
            log.info("장바구니 유효성검사 에러");
            log.info(bindingResult.getAllErrors());

            List<FieldError> fieldErrorList = bindingResult.getFieldErrors();

            StringBuilder stringBuilder = new StringBuilder();

            for (FieldError error : fieldErrorList) {
                // StringBuilder 객체에 에러 메시지를 담는다.
                stringBuilder.append(error.getDefaultMessage());
            }

            // 입력된 에러를 다시 보여주기 위해 반환값으로 에러내용을 반환
            return new ResponseEntity<>(stringBuilder.toString(), HttpStatus.BAD_REQUEST);
        }

        // 로그아웃이 되어 principal이 null일 경우 처리
        if (principal == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String memberEmail = principal.getName();  // 로그인한 사용자의 이메일
        log.info("로그인한 사용자 이메일 : " + memberEmail);
        log.info(principal.toString());

        Long roomCartItemNum = null;

        try {
            // RoomMenuCartInsert 메서드 호출 시, 이메일과 DTO만 전달
            roomCartItemNum = roomMenuCartService.RoomMenuCartInsert(memberEmail, roomMenuCartItemDTO);
        } catch (EntityNotFoundException e) {
            // 예외 발생 시 처리
            log.error("장바구니에 아이템을 추가할 수 없습니다. : " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            // 수량이 잘못된 경우 예외 처리
            log.error("잘못된 수량 입력: " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        // 성공적으로 처리되면 생성된 roomCartItemNum을 반환
        return new ResponseEntity<>(roomCartItemNum, HttpStatus.CREATED);

    }

    /***************************************************
     *
     * 메소드명   : getRoomMenuCartItems
     * 기능      : 로그인한 회원의 장바구니 아이템 목록을 조회
     * 작성자    : 김윤겸
     * 작성일    : 2025-04-08
     * 수정일    : 2025-04-10
     ****************************************************/

    @GetMapping("/cartlist")
    public String getRoomMenuCartItems(
            Principal principal,
            Model model,
            Pageable pageable,
            RoomMenuDTO roomMenuDTO,
            @AuthenticationPrincipal CustomMemberDetails customMemberDetails) {

        log.info("장바구니 리스트 컨트롤러 진입");

        if (principal == null) {
            log.info("로그인되지 않은 사용자의 장바구니 접근 시도");
            return "redirect:/member/login";
        }

        String email = principal.getName(); // 또는 customMemberDetails.getUsername();
        log.info("로그인한 사용자: {}", email);

        // 장바구니 아이템 리스트 조회
        Page<RoomMenuCartDetailDTO> cartDetailDTOList = roomMenuCartService.RoomMenuCartItemList(email, pageable);
        boolean isCartEmpty = cartDetailDTOList == null || cartDetailDTOList.isEmpty();

        //  쿠폰 리스트 가져오기
        List<CouponDTO> couponList = couponService.getCoupons(email);

        // 모델에 데이터 추가
        model.addAttribute("cartDetailDTOList", cartDetailDTOList);
        model.addAttribute("isCartEmpty", isCartEmpty);
        model.addAttribute("roomMenuDTO", roomMenuDTO);
        model.addAttribute("couponList", couponList); //


        log.info("장바구니 전체 아이템 수: {}", cartDetailDTOList.getTotalElements());
        log.info("페이지당 아이템 수: {}", cartDetailDTOList.getSize());
        log.info("현재 페이지 번호: {}", cartDetailDTOList.getNumber());

        for (RoomMenuCartDetailDTO dto : cartDetailDTOList.getContent()) {
            log.info("메뉴 이름: {}, 가격: {}", dto.getRoomMenuCartDetailMenuItemName(), dto.getRoomMenuCartDetailMenuItemPrice());
        }

        return "roommenu/cartlist";
    }


    /***************************************************
     *
     * 메소드명   : verifyRoomMenuCartItem
     * 기능      : 특정 회원의 장바구니 아이템이 해당 회원의 카트에 속하는지 검증
     * 작성자    : 김윤겸
     * 작성일    : 2025-04-08
     * 수정일    : 2025-04-10
     ****************************************************/

    @GetMapping("/verify/{roomMenuCartItemNum}")
    public ResponseEntity<Boolean> verifyRoomMenuCartItem(@PathVariable Long roomMenuCartItemNum,
                                                          @RequestParam String email) {
        try {
            boolean isVerified = roomMenuCartService.verificationRoomMenuCartItem(roomMenuCartItemNum, email);
            return ResponseEntity.ok(isVerified);  // 200 OK 응답 반환
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);  // 예외 발생 시 400 Bad Request 반환
        }
    }

    /***************************************************
     *
     * 메소드명   : modifyRoomMenuCartItemAmount
     * 기능      : 장바구니 아이템의 수량을 업데이트
     * 작성자    : 김윤겸
     * 작성일    : 2025-04-08
     * 수정일    : 2025-04-10
     ****************************************************/

    @PutMapping("/orderpage/read/{roomMenuCartItemNum}/{roomMenuCartItemAmount}")
    public ResponseEntity modifyRoomMenuCartItemAmount(
            @PathVariable("roomMenuCartItemNum") Long roomMenuCartItemNum,
            @PathVariable("roomMenuCartItemAmount") Integer roomMenuCartItemAmount,
            Principal principal) {
        log.info("장바구니 수량 변경 컨트롤러 진입" + roomMenuCartItemNum);
        log.info("해당 장바구니의 아이템 번호" + roomMenuCartItemNum);
        log.info("장바구니에 있는 해당 수량" + roomMenuCartItemAmount);

        // 수량이 0 이하일 경우 예외 처리
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

    /***************************************************
     *
     * 메소드명   : RoomMenuCartItemDeleteItem
     * 기능      : 장바구니에서 특정 아이템을 삭제
     * 작성자    : 김윤겸
     * 작성일    : 2025-04-08
     * 수정일    : 2025-04-10
     *
     ****************************************************/

    @DeleteMapping("/roomMenuCartItemDelete/{roomCartItemNum}")
    public ResponseEntity<?> RoomMenuCartItemDeleteItem(@PathVariable Long roomCartItemNum, Principal principal) {
        log.info("장바구니 삭제 컨트롤러 진입 - 삭제할 번호: " + roomCartItemNum);
        log.info("현재 로그인 사용자: " + (principal != null ? principal.getName() : "null"));

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

    @PostMapping("/apply-coupon")
    public ResponseEntity<?> applyCouponToCart(@RequestParam String email, @RequestParam Long couponNum) {
        int discountedTotal = roomMenuCartService.getTotalPriceWithCoupon(email, couponNum);
        return ResponseEntity.ok(discountedTotal);
    }
}
