package com.sixthsense.hexastay.repository;

import com.sixthsense.hexastay.entity.RoomMenuOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomMenuOrderItemRepository extends JpaRepository<RoomMenuOrderItem,Long> {

    // 주문 정보가 있는지 확인
    boolean existsByRoomMenuRoomMenuNum(Long roomMenuNum);

    // 연결된 주문 내역을 삭제
    void deleteByRoomMenu_RoomMenuNum(Long roomMenuNum);

}
