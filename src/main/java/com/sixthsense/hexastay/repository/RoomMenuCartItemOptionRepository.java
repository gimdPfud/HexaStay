package com.sixthsense.hexastay.repository;

import com.sixthsense.hexastay.entity.RoomMenuCartItem;
import com.sixthsense.hexastay.entity.RoomMenuCartItemOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomMenuCartItemOptionRepository extends JpaRepository<RoomMenuCartItemOption, Long> {

    List<RoomMenuCartItemOption> findDistinctByRoomMenuCartItem_RoomMenuCartItemNum(Long roomMenuCartItemNum);

    // 아이템 찾기
    List<RoomMenuCartItemOption> findByRoomMenuCartItem(RoomMenuCartItem cartItem);
}
