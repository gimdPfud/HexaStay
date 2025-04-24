package com.sixthsense.hexastay.controller;

import com.sixthsense.hexastay.dto.CouponDTO;
import com.sixthsense.hexastay.repository.MemberRepository;
import com.sixthsense.hexastay.service.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller  // 여기 변경!
@RequestMapping("/roommenu/coupon")
@RequiredArgsConstructor
@Log4j2

public class CouponController {

    private final CouponService couponService;
    private final MemberRepository memberRepository;

    @GetMapping("/insert")
    public String createCouponGet() {
        return "roommenu/coupon/insert"; // templates/coupon/insert.html
    }

    @ResponseBody
    @PostMapping("/insert")
    public ResponseEntity<?> createCouponPost(@RequestBody CouponDTO dto) {
        couponService.createCoupon(dto);
        return ResponseEntity.ok("쿠폰 발급 완료");
    }

    @GetMapping("/list")
    public ResponseEntity<?> getCoupons(@RequestParam String email) {
        List<CouponDTO> list = couponService.getCoupons(email);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/apply")
    @ResponseBody
    public Map<String, Integer> applyCoupon(@RequestParam String email, @RequestParam Long couponNum) {
        Integer original = couponService.getCartTotal(memberRepository.findByMemberEmail(email));
        Integer finalPrice = couponService.getTotalPriceWithCoupon(email, couponNum);
        int discount = original - finalPrice;

        Map<String, Integer> result = new HashMap<>();
        result.put("discount", discount);
        result.put("finalTotal", finalPrice);
        return result;
    }
}

