package com.sixthsense.hexastay.controller;
import com.sixthsense.hexastay.dto.CouponDTO;
import com.sixthsense.hexastay.entity.Coupon;
import com.sixthsense.hexastay.enums.CouponType;
import com.sixthsense.hexastay.repository.CouponRepository;
import com.sixthsense.hexastay.repository.MemberRepository;
import com.sixthsense.hexastay.service.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller  // 여기 변경!
@RequestMapping("/roommenu/coupon")
@RequiredArgsConstructor
@Log4j2


public class CouponController {

    private final MemberRepository memberRepository;
    private final CouponService couponService;

    @GetMapping("/insert")
    public String createCouponGet(Model model, Principal principal) { // ★ Model과 Principal 파라미터 추가
        log.info("쿠폰 발급 받기 페이지 컨트롤러 진입 (GET)");

        // (추가) 로그인한 사용자 정보가 있다면 이메일을 모델에 추가
        if (principal != null) {
            String userEmail = principal.getName(); // Spring Security를 통해 현재 로그인한 사용자의 이름(보통 이메일 또는 아이디)을 가져옵니다.
            log.info("로그인한 사용자 이메일 확인: {}", userEmail);
            model.addAttribute("currentUserEmail", userEmail); // "currentUserEmail"이라는 이름으로 모델에 이메일 저장
        } else {
            log.info("비로그인 사용자가 쿠폰 발급 페이지 접근");
            model.addAttribute("currentUserEmail", ""); // 로그인하지 않은 경우 빈 문자열 전달
        }

        return "roommenu/coupon/insert"; // templates/roommenu/coupon/insert.html 템플릿을 반환
    }

    @ResponseBody
    @PostMapping("/insert")
    public ResponseEntity<?> createCouponPost(@RequestBody CouponDTO dto) {
        log.info("쿠폰 발급 처리 컨트롤러 진입 (POST)");
        try {
            couponService.createCoupon(dto);
            log.info("쿠폰 발급 성공: 회원 {}", dto.getMemberEmail());
            return ResponseEntity.ok("쿠폰이 성공적으로 발급되었습니다.");
        } catch (IllegalStateException | IllegalArgumentException e) {
            log.warn("쿠폰 발급 실패 (서비스 예외): {}", e.getMessage(), e);
            Map<String, String> errorResponse = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            log.error("쿠폰 발급 중 예상치 못한 오류 발생", e);
            Map<String, String> errorResponse = Map.of("error", "쿠폰 발급 중 서버 오류 발생");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/list")
    public ResponseEntity<?> getCoupons(@RequestParam String email) {
        log.info("사용자 쿠폰 목록 조회 컨트롤러 진입: {}", email);
        List<CouponDTO> list = couponService.getCoupons(email);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/apply")
    @ResponseBody
    public ResponseEntity<?> applyCoupon(@RequestParam String email, @RequestParam Long couponNum) {
        log.info("쿠폰 적용 컨트롤러 진입 - email: {}, couponNum: {}", email, couponNum);
        try {
            Integer original = couponService.getCartTotal(memberRepository.findByMemberEmail(email));
            Integer finalPrice = couponService.getTotalPriceWithCoupon(email, couponNum);
            int discount = original - finalPrice;

            Map<String, Integer> result = new HashMap<>();
            result.put("discount", discount);
            result.put("finalTotal", finalPrice);
            log.info("쿠폰 적용 결과 반환 (성공): {}", result);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            log.warn("쿠폰 적용 실패 (서비스 예외): {}", e.getMessage(), e);
            Map<String, String> errorResponse = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            log.error("쿠폰 적용 중 예상치 못한 오류 발생", e);
            Map<String, String> errorResponse = Map.of("error", "쿠폰 적용 중 서버 오류 발생");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/exists")
    public ResponseEntity<Map<String, Boolean>> checkMemberExists(@RequestBody Map<String, String> requestBody) {
        log.info("회원 존재 여부 체크 컨트롤러 진입");
        String email = requestBody.get("email");
        boolean exists = memberRepository.existsByMemberEmail(email);

        Map<String, Boolean> result = new HashMap<>();
        result.put("exists", exists);
        log.info("회원 {} 존재 여부: {}", email, exists);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/already-issued")
    @ResponseBody
    public ResponseEntity<Map<String, Boolean>> checkCouponAlreadyIssued(
            @RequestParam String email,
            @RequestParam String couponType
    ) {
        log.info("쿠폰 이미 발급 여부 체크 컨트롤러 진입 - email: {}, couponType: {}", email, couponType);
        CouponType typeEnum;
        try {
            typeEnum = CouponType.valueOf(couponType);
        } catch (IllegalArgumentException ex) {
            log.warn("유효하지 않은 쿠폰 타입 문자열 수신: {}", couponType, ex);
            return ResponseEntity.ok(Map.of("issued", false));
        }

        boolean issued = couponService.hasCoupon(email, typeEnum);

        Map<String, Boolean> response = new HashMap<>();
        response.put("issued", issued);

        log.info("회원 {} 쿠폰 {} 이미 발급 여부: {}", email, typeEnum.name(), issued);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/types")
    public ResponseEntity<List<Map<String, String>>> getCouponTypes() {
        log.info("쿠폰 타입 목록 조회 컨트롤러 진입");
        List<Map<String, String>> types = Arrays.stream(CouponType.values())
                .map(couponType -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("name", couponType.name());
                    map.put("code", couponType.getCode());
                    return map;
                })
                .collect(Collectors.toList());
        log.info("쿠폰 타입 {}개 반환", types.size());
        return ResponseEntity.ok(types);
    }
}


