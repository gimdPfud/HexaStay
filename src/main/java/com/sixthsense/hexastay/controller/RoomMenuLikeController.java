package com.sixthsense.hexastay.controller;

import com.sixthsense.hexastay.config.Security.CustomMemberDetails;
import com.sixthsense.hexastay.service.RoomMenuLikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@Log4j2

public class RoomMenuLikeController {

    private final RoomMenuLikeService roomMenuLikeService;

    // 좋아요
    @PostMapping("/roomMenu/orderpage/like/{roomMenuNum}")
    @ResponseBody
    public ResponseEntity<Integer> RoomMenuLikePost(@PathVariable Long roomMenuNum,
                                                    @AuthenticationPrincipal CustomMemberDetails customMemberDetails) {
        log.info("룸메뉴 좋아요 컨트롤러 진입");
        log.info("로그인한 사용자" + customMemberDetails.getMember().getMemberEmail());
        String email = customMemberDetails.getName();
        int likes = roomMenuLikeService.roomMenuLike(roomMenuNum, email);
        return ResponseEntity.ok(likes);
    }

    // 좋아요 취소
    @PostMapping("/roomMenu/orderpage/cancellike/{roomMenuNum}")
    @ResponseBody
    public ResponseEntity<Integer> RoomMenuLikeCancelPost(@PathVariable Long roomMenuNum, Principal principal) {
        log.info("룸메뉴 좋아요 캔슬 컨트롤러 진입");
        String email = principal.getName();
        int likes = roomMenuLikeService.roomMenuLikeCancel(roomMenuNum, email);
        return ResponseEntity.ok(likes);
    }

    // 싫어요
    @PostMapping("/roomMenu/orderpage/dislike/{roomMenuNum}")
    @ResponseBody
    public ResponseEntity<Integer> RoomMenuDisLikePost(@PathVariable Long roomMenuNum, Principal principal) {
        log.info("룸 메뉴 싫어요 컨트롤러 진입");
        String email = principal.getName();
        int dislikes = roomMenuLikeService.roomMenuDisLike(roomMenuNum, email);
        return ResponseEntity.ok(dislikes);
    }

    // 싫어요 취소
    @PostMapping("/roomMenu/orderpage/cancledislike/{roomMenuNum}")
    @ResponseBody
    public ResponseEntity<Integer> RoomMenuCancelDisLikePost(@PathVariable Long roomMenuNum, Principal principal) {
        log.info("룸 메뉴 싫어요 취소 컨트롤러 진입");
        String email = principal.getName();
        int dislikes = roomMenuLikeService.roomMenuDisLikeCancel(roomMenuNum, email);
        return ResponseEntity.ok(dislikes);
    }
}
