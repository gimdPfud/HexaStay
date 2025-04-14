package com.sixthsense.hexastay.service.impl;

import com.sixthsense.hexastay.entity.Member;
import com.sixthsense.hexastay.entity.RoomMenu;
import com.sixthsense.hexastay.entity.RoomMenuLike;
import com.sixthsense.hexastay.repository.MemberRepository;
import com.sixthsense.hexastay.repository.RoomMenuRepository;
import com.sixthsense.hexastay.service.RoomMenuLikeService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2

public class RoomMenuLikeServiceImpl implements RoomMenuLikeService {

    private final RoomMenuRepository roomMenuRepository;


    @Override
    public Integer roomMenuLike(Long roomMenuNum, String memberEmail) {
        log.info("좋아요 서비스 진입" + memberEmail);
        RoomMenu menu = roomMenuRepository.findByRoom_Member_MemberEmail(memberEmail);

        menu.setRoomMenuLikes(menu.getRoomMenuLikes() + 1);
        roomMenuRepository.save(menu);

        return menu.getRoomMenuLikes();
    }

    @Override
    public Integer roomMenuLikeCancel(Long roomMenuNum, String memberEmail) {
        log.info("좋아요 캔슬 서비스 진입" + roomMenuNum);
        log.info("좋아요 캔슬 서비스 진입" + memberEmail);
        RoomMenu menu = roomMenuRepository.findByRoom_Member_MemberEmail(memberEmail);

        // 좋아요 수 감소 (최소 0)
        menu.setRoomMenuLikes(Math.max(menu.getRoomMenuLikes() - 1, 0));
        roomMenuRepository.save(menu);

        return menu.getRoomMenuLikes();
    }

    @Override
    public Integer roomMenuDisLike(Long roomMenuNum, String memberEmail) {
        log.info("싫어요 서비스 진입" + roomMenuNum);
        log.info("싫어요 서비스 진입" + memberEmail);
        RoomMenu menu = roomMenuRepository.findByRoom_Member_MemberEmail(memberEmail);

        // 싫어요 수 증가
        menu.setRoomMenuDisLikes(menu.getRoomMenuDisLikes() + 1);
        roomMenuRepository.save(menu);

        return menu.getRoomMenuDisLikes();
    }

    @Override
    public Integer roomMenuDisLikeCancel(Long roomMenuNum, String memberEmail) {
        log.info("싫어요 캔슬 서비스 진입" + roomMenuNum);
        log.info("싫어요 캔슬 서비스 진입" + memberEmail);
        RoomMenu menu = roomMenuRepository.findByRoom_Member_MemberEmail(memberEmail);

        // 싫어요 수 감소 (최소 0)
        menu.setRoomMenuDisLikes(Math.max(menu.getRoomMenuDisLikes() - 1, 0));
        roomMenuRepository.save(menu);

        return menu.getRoomMenuDisLikes();
    }
}
