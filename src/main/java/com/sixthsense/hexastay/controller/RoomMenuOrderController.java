package com.sixthsense.hexastay.controller;

/* 클래스명 : RoomMenuController
 * 기능 : 룸서비스(메뉴)와 관련된 컨트롤러
 *        룸서비스의 메뉴를 관리하는 다양한 페이지를 처리하는 컨트롤러.
 *        메뉴 목록 조회, 상세보기, 등록, 수정, 삭제 등의 기능을 담당
 * 작성자 : 김윤겸
 * 작성일 : 2025-04-09
 * 수정일 : 2025-00-00 입출력변수설계 : 김윤겸 */

import com.sixthsense.hexastay.dto.RoomMenuDTO;
import com.sixthsense.hexastay.dto.RoomMenuOrderDTO;
import com.sixthsense.hexastay.service.RoomMenuCartService;
import com.sixthsense.hexastay.service.RoomMenuOrderService;
import com.sixthsense.hexastay.service.RoomMenuService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
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

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Map;

import static com.sixthsense.hexastay.util.PaginationUtil.Pagination;

@Controller
@RequiredArgsConstructor
@Log4j2

public class RoomMenuOrderController {

    private final RoomMenuOrderService roomMenuOrderService;

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
                                BindingResult bindingResult , Principal principal, HttpServletRequest request){
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
            log.info(String.format("주문이 완료됨 - DTO: %s, Email: %s", roomMenuOrderDTO, email));


        } catch (IllegalStateException e) {
            // 재고 부족 등의 IllegalStateException 발생 시 BAD_REQUEST로 에러 메시지 반환
            log.info("재고가 부족합니다.");
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        // 주문 생성 성공 시, 주문 번호 반환 (HTTP 200 OK)
        return new ResponseEntity<Long>(roomMenuOrderNum, HttpStatus.OK);
    }

}
