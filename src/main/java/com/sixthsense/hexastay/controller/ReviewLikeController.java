package com.sixthsense.hexastay.controller;

import com.sixthsense.hexastay.service.ReviewLikeService;
import com.sixthsense.hexastay.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/reviewlike")
public class ReviewLikeController {

    private final ReviewLikeService reviewLikeService;

    // (추가) 좋아요 Ajax 요청 처리
    @GetMapping("/toggle")
    public Map<String, Object> toggleLike(@RequestBody Map<String, Long> request, Principal principal) {
        Map<String, Object> response = new HashMap<>();

        if (principal == null) {
            response.put("status", "unauthorized");
            return response;
        }

        Long reviewNo = request.get("reviewNo");
        String memberId = principal.getName();

        log.info("리뷰 좋아요 요청: reviewNo={}, memberId={}", reviewNo, memberId);

        int result = reviewLikeService.toggleReviewLike(reviewNo, memberId);

        if (result == -1) {
            response.put("status", "duplicated");
        } else {
            response.put("status", "ok");
            response.put("likeCount", result);
        }

        log.info("끝");
        return response;
    }
}