package com.sixthsense.hexastay.repository;

import com.sixthsense.hexastay.dto.RoomMenuCartDetailDTO;
import com.sixthsense.hexastay.dto.RoomMenuCartItemDTO;
import com.sixthsense.hexastay.entity.Review;
import com.sixthsense.hexastay.entity.RoomMenu;
import com.sixthsense.hexastay.entity.RoomMenuCart;
import com.sixthsense.hexastay.entity.RoomMenuCartItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomMenuCartItemRepository extends JpaRepository<RoomMenuCartItem, Long> {

    @Query("select r from RoomMenuCartItem r")
    public Page<RoomMenuCartItem> findAll(Pageable pageable);


    // RoomMenuCart와 RoomMenu를 기반으로 RoomMenuCartItem을 찾는 메서드
    Optional<RoomMenuCartItem> findByRoomMenuCartAndRoomMenu(RoomMenuCart roomMenuCart, RoomMenu roomMenu);

    @Query("SELECT DISTINCT new com.sixthsense.hexastay.dto.RoomMenuCartDetailDTO(rmci.roomMenuCartItemNum, rmi.roomMenuName, rmi.roomMenuPrice, rmci.roomMenuCartItemAmount) " +
            "FROM RoomMenuCartItem rmci " +
            "JOIN RoomMenu rmi ON rmci.roomMenu.roomMenuNum = rmi.roomMenuNum " +
            "WHERE rmci.roomMenuCart.member.memberEmail = :email " +
            "ORDER BY rmci.roomMenuCartItemNum DESC")
    public Page<RoomMenuCartDetailDTO> findByCartDetailDTOList(@Param("email") String email, Pageable pageable);


}

