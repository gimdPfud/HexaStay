package com.sixthsense.hexastay.controller;

import com.sixthsense.hexastay.dto.RoomMenuCartDTO;
import com.sixthsense.hexastay.dto.RoomMenuCartItemDTO;
import com.sixthsense.hexastay.dto.RoomMenuDTO;
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

    /***************************************************
     *
     * 메소드명   : addRoomMenuCartItem
     * 기능      : 장바구니에 메뉴 아이템을 추가하거나 수량을 업데이트
     * 작성자    : 김윤겸
     * 작성일    : 2025-04-08
     *
     ****************************************************/

    @PostMapping("/cart")
    public ResponseEntity order(@Valid RoomMenuDTO roomMenuDTO,
                                BindingResult bindingResult,
                                Principal principal) {
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
            roomCartItemNum = roomMenuCartService.RoomMenuCartInsert(roomCartItemNum, memberEmail, roomMenuDTO);
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
     *
     ****************************************************/

    @GetMapping("/cartlist")
    public ResponseEntity<Page<RoomMenuCartItemDTO>> getRoomMenuCartItems(Principal principal, Pageable pageable) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);  // 로그인되지 않은 경우 401 Unauthorized 응답 반환
        }

        String email = principal.getName();  // 로그인된 사용자의 이메일
        log.info("로그인된 사용자의 이메일" + principal.getName());
        if (email == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);  // 이메일이 없는 경우 400 Bad Request 반환
        }

        try {
            Page<RoomMenuCartItemDTO> roomMenuCartItemDTOPage = roomMenuCartService.RoomMenuCartItemList(email, pageable);
            return ResponseEntity.ok(roomMenuCartItemDTOPage);  // 200 OK 응답 반환
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);  // 예외 발생 시 400 Bad Request 반환
        }
    }

    /***************************************************
     *
     * 메소드명   : verifyRoomMenuCartItem
     * 기능      : 특정 회원의 장바구니 아이템이 해당 회원의 카트에 속하는지 검증
     * 작성자    : 김윤겸
     * 작성일    : 2025-04-08
     *
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
     * 메소드명   : updateRoomMenuCartItemAmount
     * 기능      : 장바구니 아이템의 수량을 업데이트
     * 작성자    : 김윤겸
     * 작성일    : 2025-04-08
     *
     ****************************************************/

    @PutMapping("/modify/{roomMenuCartItemNum}")
    public ResponseEntity<Void> modifyRoomMenuCartItemAmount(@PathVariable Long roomMenuCartItemNum,
                                                             @RequestParam Integer roomMenuCartItemAmount) {
        try {
            roomMenuCartService.updateRoomMenuCartItemAmount(roomMenuCartItemNum, roomMenuCartItemAmount);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();  // 204 No Content 응답 반환
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();  // 예외 발생 시 400 Bad Request 반환
        }
    }

    /***************************************************
     *
     * 메소드명   : removeRoomMenuCartItem
     * 기능      : 장바구니에서 특정 아이템을 삭제
     * 작성자    : 김윤겸
     * 작성일    : 2025-04-08
     * 수정일    :
     *
     ****************************************************/

    @DeleteMapping("/remove/{roomMenuCartItemNum}")
    public ResponseEntity<Void> removeRoomMenuCartItem(@PathVariable Long roomMenuCartItemNum) {
        try {
            roomMenuCartService.RoomCartMenuCartItemDelete(roomMenuCartItemNum);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();  // 204 No Content 응답 반환
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();  // 예외 발생 시 400 Bad Request 반환
        }
    }

}


//    // 장바구니에 아이템 추가
//    @PostMapping("/cart")
//    public RoomMenuCartDTO addRoomMenuToCart(@RequestParam Long memberNum,
//                                             @RequestParam Long roomMenuNum,
//                                             @RequestParam Integer amount) {
//        log.info("장바구니 담기 컨트롤러 진입");
//        return roomMenuCartService.insertRoomMenuCart(memberNum, roomMenuNum, amount);
//    }
//
//    // 룸서비스 상품 리스트 검색
//    @GetMapping("/cart/list")
//    public Page<RoomMenuDTO> getRoomMenuList(Pageable pageable,
//                                             @RequestParam String type,
//                                             @RequestParam(required = false) String keyword,
//                                             @RequestParam(required = false) String category) {
//        log.info("장바구니 리스트 컨트롤러 진입");
//        return roomMenuCartService.RoomMenuList(pageable, type, keyword, category);
//    }
//
//    // (옵션) 장바구니 조회 기능 추가
//    @GetMapping("/cart/{memberNum}")
//    public RoomMenuCartDTO getRoomMenuCart(@PathVariable Long memberNum) {
//        return roomMenuCartService.getCartByMember(memberNum);
//    }
