package com.sixthsense.hexastay.service.impl;

import com.sixthsense.hexastay.entity.Member;
import com.sixthsense.hexastay.entity.RoomMenu;
import com.sixthsense.hexastay.entity.RoomMenuLike;
import com.sixthsense.hexastay.repository.MemberRepository;
import com.sixthsense.hexastay.repository.RoomMenuLikeRepository;
import com.sixthsense.hexastay.repository.RoomMenuRepository;
import com.sixthsense.hexastay.service.RoomMenuLikeService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2

public class RoomMenuLikeServiceImpl implements RoomMenuLikeService {

    private final RoomMenuRepository roomMenuRepository;
    private final MemberRepository memberRepository;
    private final RoomMenuLikeRepository roomMenuLikeRepository;


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

    @Override
    public Integer roomMenuDisLike(Long roomMenuNum, String memberEmail) {
        log.info("싫어요 서비스 진입" + roomMenuNum);
        log.info("싫어요 서비스 진입" + memberEmail);
        Member member = memberRepository.findByMemberEmail(memberEmail);
        RoomMenu menu = roomMenuRepository.findById(roomMenuNum).orElseThrow();

        Optional<RoomMenuLike> optional = roomMenuLikeRepository.findByMember_MemberEmailAndRoomMenu(memberEmail, menu);
        if (optional.isPresent()) {
            RoomMenuLike like = optional.get();
            like.setRoomMenuLikedCheck(false); // 싫어요로 전환
            roomMenuLikeRepository.save(like);
        } else {
            RoomMenuLike newLike = new RoomMenuLike();
            newLike.setRoomMenu(menu);
            newLike.setMember(member);
            newLike.setRoomMenuLikedCheck(false); // 싫어요 등록
            roomMenuLikeRepository.save(newLike);
        }

        return roomMenuLikeRepository.countByRoomMenuAndRoomMenuLikedCheck(menu, false).intValue();
    }

    @Override
    public Integer roomMenuDisLikeCancel(Long roomMenuNum, String memberEmail) {
        log.info("싫어요 캔슬 서비스 진입" + roomMenuNum);
        log.info("싫어요 캔슬 서비스 진입" + memberEmail);
        Member member = memberRepository.findByMemberEmail(memberEmail);
        RoomMenu menu = roomMenuRepository.findById(roomMenuNum).orElseThrow();

        Optional<RoomMenuLike> optional =
                roomMenuLikeRepository.findByMember_MemberEmailAndRoomMenu(memberEmail, menu);
        if (optional.isPresent() && Boolean.FALSE.equals(optional.get().getRoomMenuLikedCheck())) {
            roomMenuLikeRepository.delete(optional.get()); // 싫어요 → 삭제
        }

        return roomMenuLikeRepository.countByRoomMenuAndRoomMenuLikedCheck(menu, false).intValue();
    }
}
