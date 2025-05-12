package com.sixthsense.hexastay.controller;

import com.sixthsense.hexastay.service.ReorderService;
import com.sixthsense.hexastay.util.exception.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/reorder") // API 경로 설정
@RequiredArgsConstructor
@Log4j2

public class ReorderController {

    private final ReorderService reorderService;

    @PostMapping("/{orderNum}")
    // @ResponseBody // @RestController를 사용하면 이 어노테이션은 필요 없습니다.
    public ResponseEntity<Map<String, Object>> handleReorderRequest(@PathVariable("orderNum") Long orderNum,
                                                                    Principal principal) {


        Map<String, Object> response = new HashMap<>();

        /*if (principal == null) {
            log.warn("(컨트롤러) 재주문 요청 실패: 로그인되지 않은 사용자 접근, 주문번호 - {}", orderNum);
            response.put("success", false);
            response.put("message", "로그인이 필요합니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }*/

        String userEmail = principal.getName();
        log.info("(컨트롤러) 재주문 요청 수신 - 주문번호: {}, 사용자 이메일: {}", orderNum, userEmail);

        try {
            reorderService.addPastOrderItemsToCart(orderNum, userEmail);
            log.info("(컨트롤러) 재주문 상품 장바구니 추가 성공 - 주문번호: {}, 사용자: {}", orderNum, userEmail);
            response.put("success", true);
            response.put("message", "선택하신 주문의 상품들이 장바구니에 성공적으로 담겼습니다. 장바구니를 확인해주세요.");
            return ResponseEntity.ok(response);
        } catch (UserNotFoundException | OrderNotFoundException | UnauthorizedOrderAccessException |
                 ProductNotFoundException e) { // OptionNotFoundException 제거 (서비스에서 발생 안 시키므로)
            log.warn("(컨트롤러) 재주문 처리 실패 (데이터 조회 오류 또는 권한 없음): {}", e.getMessage());
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (SoldOutException | IllegalStateException e) {
            log.warn("(컨트롤러) 재주문 처리 실패 (재고 부족 또는 규칙 위반): {}", e.getMessage());
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            log.error("(컨트롤러) 재주문 처리 중 예상치 못한 서버 오류 발생 - 주문번호: {}, 사용자: {}", orderNum, userEmail, e);
            response.put("success", false);
            response.put("message", "재주문 처리 중 서버 내부 오류가 발생했습니다. 관리자에게 문의해주세요."); // 사용자에게 더 친절한 메시지
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
