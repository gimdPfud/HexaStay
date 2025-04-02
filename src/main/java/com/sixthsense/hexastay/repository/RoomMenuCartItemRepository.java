package com.sixthsense.hexastay.repository;

import com.sixthsense.hexastay.entity.Review;
import com.sixthsense.hexastay.entity.RoomMenuCartItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomMenuCartItemRepository extends JpaRepository<RoomMenuCartItem, Long> {

    @Query("select r from RoomMenuCartItem r")
    public Page<RoomMenuCartItem> findAll(Pageable pageable);

    // 장바구니 항목 번호로 항목 조회
    RoomMenuCartItem findByRoomMenuCartItemNum(Long roomMenuCartItemNum);
}
