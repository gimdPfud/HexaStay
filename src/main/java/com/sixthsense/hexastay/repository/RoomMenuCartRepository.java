package com.sixthsense.hexastay.repository;

import com.sixthsense.hexastay.entity.RoomMenuCart;
import com.sixthsense.hexastay.entity.RoomMenuCartItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoomMenuCartRepository extends JpaRepository<RoomMenuCart, Long> {
    @Query("select r from RoomMenuCart r")
    public Page<RoomMenuCart> findAll(Pageable pageable);

    // 장바구니 번호로 장바구니 조회
    RoomMenuCart findByRoomMenuCartNum(Long roomMenuCartNum);

    // 유저찾기
    Optional<RoomMenuCart> findByMember_MemberNum(Long memberNum);

    public RoomMenuCart findByMember_MemberEmail(String email);

}
