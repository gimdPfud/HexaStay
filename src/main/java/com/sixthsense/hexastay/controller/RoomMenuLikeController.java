package com.sixthsense.hexastay.controller;

import com.sixthsense.hexastay.config.Security.CustomMemberDetails;
import com.sixthsense.hexastay.entity.RoomMenu;
import com.sixthsense.hexastay.entity.RoomMenuLike;
import com.sixthsense.hexastay.repository.RoomMenuLikeRepository;
import com.sixthsense.hexastay.repository.RoomMenuRepository;
import com.sixthsense.hexastay.service.RoomMenuLikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Log4j2

/**************************************************
 * 클래스명 : RoomMenuLikeController
 * 기능   : 룸서비스 메뉴에 대한 '좋아요' 및 '싫어요' 관련 기능을 처리하는 컨트롤러입니다.
 * 사용자의 메뉴 좋아요/싫어요 설정, 해제 및 관련 상태 조회 기능을 제공합니다.
 * 작성자 : 김윤겸
 * 작성일 : 2025-04-14
 * 수정일 : 2025-05-09
 * 입출력 변수 설계 : 김윤겸
 **************************************************/

public class RoomMenuLikeController {
    private final RoomMenuLikeRepository roomMenuLikeRepository;
    private final RoomMenuLikeService roomMenuLikeService;
    private final RoomMenuRepository roomMenuRepository;

    /**************************************************
     * 메소드명 : RoomMenuLikePost
     * 룸서비스 메뉴 좋아요 처리
     * 기능: 특정 룸서비스 메뉴(roomMenuNum)에 대해 현재 로그인한 사용자의 '좋아요'
     * 상태를 토글(설정 또는 해제)하고,
     * 해당 메뉴의 업데이트된 총 좋아요 수를 반환합니다.
     * 작성자 : 김윤겸
     * 등록일 : 2025-04-14
     * 수정일 : 2025-04-17
     **************************************************/

    // 좋아요
    @PostMapping("/roommenu/orderpage/like/{roomMenuNum}")
    @ResponseBody
    public ResponseEntity<Integer> RoomMenuLikePost(@PathVariable Long roomMenuNum,
                                                    @AuthenticationPrincipal CustomMemberDetails customMemberDetails) {
        log.info("룸메뉴 좋아요 컨트롤러 진입");
        log.info("로그인한 사용자" + customMemberDetails.getMember().getMemberEmail());
        String email = customMemberDetails.getName();

        int likes = roomMenuLikeService.roomMenuLike(roomMenuNum, email);
        return ResponseEntity.ok(likes);
    }

    /**************************************************
     * 메소드명 : getRoomMenuLikeStatus
     * 룸서비스 메뉴 좋아요 상태 및 총 개수 조회
     * 기능: 특정 룸서비스 메뉴(roomMenuNum)에 대해, 현재 로그인한 사용자의 '좋아요' 여부와
     * 해당 메뉴의 총 '좋아요' 개수를 조회하여 반환합니다. 비로그인 사용자의 경우 개인의 '좋아요'
     * 상태는 false로 처리됩니다.
     * 작성자 : 김윤겸
     * 등록일 : 2025-04-14
     * 수정일 : 2025-04-17
     **************************************************/

    @GetMapping("/roommenu/orderpage/status/{roomMenuNum}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getRoomMenuLikeStatus(
            @PathVariable Long roomMenuNum,
            @AuthenticationPrincipal CustomMemberDetails customMemberDetails) {
        log.info("룸메뉴 좋아요 조회 컨트롤러 진입 - roomMenuNum: {}", roomMenuNum);

        String email = null;
        if (customMemberDetails != null && customMemberDetails.getMember() != null) {
            email = customMemberDetails.getMember().getMemberEmail();
            log.info("로그인한 사용자: {}", email);
        } else {
            log.info("비로그인 사용자 또는 사용자 정보를 가져올 수 없음.");
        }

        // RoomMenu 존재 여부 확인
        Optional<RoomMenu> optionalMenu = roomMenuRepository.findById(roomMenuNum);
        if (optionalMenu.isEmpty()) {
            log.warn("요청된 RoomMenu ID {}를 찾을 수 없습니다.", roomMenuNum);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "메뉴를 찾을 수 없습니다.", "roomMenuNum", roomMenuNum));
        }

        RoomMenu menu = optionalMenu.get();
        boolean liked = false;

        // 로그인한 사용자만 개인의 '좋아요' 상태를 조회
        if (email != null) {
            Optional<RoomMenuLike> optionalLike = roomMenuLikeRepository.findByMember_MemberEmailAndRoomMenu(email, menu);
            if (optionalLike.isPresent()) {
                RoomMenuLike likeInfo = optionalLike.get();
                liked = Boolean.TRUE.equals(likeInfo.getRoomMenuLikedCheck());
            }
        }

        // 메뉴의 총 좋아요/싫어요 개수 (이 부분은 로그인 여부와 관계없이 계산)
        Long likeCount = roomMenuLikeRepository.countByRoomMenuAndRoomMenuLikedCheck(menu, true);

        Map<String, Object> response = new HashMap<>();
        response.put("liked", liked); // 비로그인 시에는 false
        response.put("likeCount", likeCount);

        return ResponseEntity.ok(response);
    }

    /**************************************************
     * 메소드명 : RoomMenuLikeCancelPost
     * 룸서비스 메뉴 좋아요 취소 처리
     * 기능: 특정 룸서비스 메뉴(roomMenuNum)에 대해 현재 로그인한 사용자가 이전에 눌렀던 '좋아요'를
     * 취소하고, 해당 메뉴의 변경된 총 좋아요 수를 반환합니다.
     * 작성자 : 김윤겸
     * 등록일 : 2025-04-15
     * 수정일 : -
     **************************************************/

    // 좋아요 취소
    @PostMapping("/roommenu/orderpage/cancellike/{roomMenuNum}")
    @ResponseBody
    public ResponseEntity<Integer> RoomMenuLikeCancelPost(@PathVariable Long roomMenuNum, Principal principal, @AuthenticationPrincipal CustomMemberDetails customMemberDetails) {
        log.info("룸메뉴 좋아요 캔슬 컨트롤러 진입");
        String email = principal.getName();
        int likes = roomMenuLikeService.roomMenuLikeCancel(roomMenuNum, email);
        return ResponseEntity.ok(likes);
    }

}
