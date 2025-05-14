package com.sixthsense.hexastay.repository;

import com.sixthsense.hexastay.entity.RoomMenu;
import com.sixthsense.hexastay.entity.RoomMenuOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoomMenuOptionRepository extends JpaRepository<RoomMenuOption, Long> {

    // 특정 룸메뉴에 대한 옵션 조회
    List<RoomMenuOption> findByRoomMenu(RoomMenu roomMenu);

    // 특정 룸메뉴 옵션 ID로 조회
    Optional<RoomMenuOption> findById(Long roomMenuOptionNum);

    // 삭제시 옵션 확인
    boolean existsByRoomMenu_RoomMenuNum(Long roomMenuNum);


}
