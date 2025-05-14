package com.sixthsense.hexastay.service.impl;

import com.sixthsense.hexastay.entity.Member;
import com.sixthsense.hexastay.entity.RoomMenu;
import com.sixthsense.hexastay.entity.RoomMenuLike;
import com.sixthsense.hexastay.repository.MemberRepository;
import com.sixthsense.hexastay.repository.RoomMenuLikeRepository;
import com.sixthsense.hexastay.repository.RoomMenuRepository;
import com.sixthsense.hexastay.service.RoomMenuLikeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2

public class RoomMenuLikeServiceImpl implements RoomMenuLikeService {

    private final RoomMenuRepository roomMenuRepository;
    private final MemberRepository memberRepository;
    private final RoomMenuLikeRepository roomMenuLikeRepository;


    /***********************************************
     * 메서드명 : roomMenuLike
     * 기능 : 특정 룸 메뉴에 대한 사용자의 좋아요 설정을 처리
     * - 이미 좋아요를 누른 경우 좋아요를 취소
     * - 싫어요를 누른 상태였다면 싫어요를 취소하고 좋아요로 변경
     * - 처음 누르는 경우 새로운 좋아요 정보를 생성
     * - 처리 후 해당 룸 메뉴의 총 좋아요 수를 반환
     * 매개변수 : Long roomMenuNum - 룸 메뉴 번호
     * String memberEmail - 사용자 이메일
     * 반환값 : Integer - 해당 룸 메뉴의 총 좋아요 수
     * 작성자 : 김윤겸
     * 작성일 : 2025-04-15
     * 수정일 : -
     * ***********************************************/

    @Override
    public Integer roomMenuLike(Long roomMenuNum, String memberEmail) {
        log.info("좋아요 서비스 진입" + memberEmail);
        log.info("좋아요 서비스 진입" + roomMenuNum);

        Member member = memberRepository.findByMemberEmail(memberEmail);
        RoomMenu menu = roomMenuRepository.findById(roomMenuNum).orElseThrow();

        Optional<RoomMenuLike> optionalLike = roomMenuLikeRepository.findByMember_MemberEmailAndRoomMenu(memberEmail, menu);

        if (optionalLike.isPresent()) {
            RoomMenuLike like = optionalLike.get();
            if (Boolean.TRUE.equals(like.getRoomMenuLikedCheck())) {
                // 이미 좋아요 상태면 → 취소
                roomMenuLikeRepository.delete(like);
            } else {
                // 싫어요였으면 → 좋아요로 전환
                like.setRoomMenuLikedCheck(true);
                roomMenuLikeRepository.save(like);
            }
        } else {
            // 처음 누르는 거면 → 새로 생성
            RoomMenuLike newLike = new RoomMenuLike();
            newLike.setMember(member);
            newLike.setRoomMenu(menu);
            newLike.setRoomMenuLikedCheck(true);
            roomMenuLikeRepository.save(newLike);
        }

        // 좋아요 수 리턴
        return roomMenuLikeRepository.countByRoomMenuAndRoomMenuLikedCheck(menu, true).intValue();
    }

    /***********************************************
     * 메서드명 : roomMenuLikeCancel
     * 기능 : 특정 룸 메뉴에 대한 사용자의 좋아요 취소 요청을 처리
     * - 해당 사용자가 해당 룸 메뉴에 좋아요를 눌렀던 경우 해당 정보를 삭제
     * - 처리 후 해당 룸 메뉴의 총 좋아요 수를 반환
     * 매개변수 : Long roomMenuNum - 룸 메뉴 번호
     * String memberEmail - 사용자 이메일
     * 반환값 : Integer - 해당 룸 메뉴의 총 좋아요 수
     * 작성자 : 김윤겸
     * 작성일 : 2025-04-15
     * 수정일 : -
     * ***********************************************/

    @Override
    public Integer roomMenuLikeCancel(Long roomMenuNum, String memberEmail) {
        log.info("좋아요 캔슬 서비스 진입" + roomMenuNum);
        log.info("좋아요 캔슬 서비스 진입" + memberEmail);
        Member member = memberRepository.findByMemberEmail(memberEmail);
        RoomMenu menu = roomMenuRepository.findById(roomMenuNum).orElseThrow();

        Optional<RoomMenuLike> optional = roomMenuLikeRepository.findByMember_MemberEmailAndRoomMenu(memberEmail, menu);
        if (optional.isPresent() && Boolean.TRUE.equals(optional.get().getRoomMenuLikedCheck())) {
            roomMenuLikeRepository.delete(optional.get()); // 좋아요 → 삭제
        }

        return roomMenuLikeRepository.countByRoomMenuAndRoomMenuLikedCheck(menu, true).intValue();
    }
}
