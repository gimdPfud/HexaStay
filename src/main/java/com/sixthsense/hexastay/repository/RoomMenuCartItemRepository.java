package com.sixthsense.hexastay.repository;

import com.sixthsense.hexastay.entity.Review;
import com.sixthsense.hexastay.entity.RoomMenu;
import com.sixthsense.hexastay.entity.RoomMenuCart;
import com.sixthsense.hexastay.entity.RoomMenuCartItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomMenuCartItemRepository extends JpaRepository<RoomMenuCartItem, Long> {

    @Query("select r from RoomMenuCartItem r")
    public Page<RoomMenuCartItem> findAll(Pageable pageable);

    // 장바구니 항목 번호로 항목 조회
    RoomMenuCartItem findByRoomMenuCartItemNum(Long roomMenuCartItemNum);

    // 장바구니에 속하는 모든 장바구니 항목 조회
    List<RoomMenuCartItem> findAllByRoomMenuCart(RoomMenuCart roomMenuCart);

    // RoomMenuCart와 RoomMenu를 기반으로 RoomMenuCartItem을 찾는 메서드
    Optional<RoomMenuCartItem> findByRoomMenuCartAndRoomMenu(RoomMenuCart roomMenuCart, RoomMenu roomMenu);
}

