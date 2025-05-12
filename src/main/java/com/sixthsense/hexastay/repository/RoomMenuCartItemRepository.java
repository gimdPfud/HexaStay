package com.sixthsense.hexastay.repository;
import com.sixthsense.hexastay.dto.RoomMenuCartDetailDTO;
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

    // 장바구니 리스트 fixme : 2025년 04.27일 오전 6시 이전으로 롤빽할수도 있음.
    @Query("SELECT new com.sixthsense.hexastay.dto.RoomMenuCartDetailDTO(" +
            "rmci.roomMenuCartItemNum, " +
            "rmi.roomMenuName, " +
            "(rmci.roomMenuCartItemPrice / rmci.roomMenuCartItemAmount), " + // <<< 수정: 총 가격 / 수량으로 개당 가격 계산
            "rmci.roomMenuCartItemAmount, " +
            "rmci.roomMenuSelectOptionName, " +
            "rmci.roomMenuSelectOptionPrice, " +
            "rmi.roomMenuImageMeta) " +
            "FROM RoomMenuCartItem rmci " +
            "JOIN RoomMenu rmi ON rmci.roomMenu.roomMenuNum = rmi.roomMenuNum " +
            "WHERE rmci.roomMenuCart.member.memberEmail = :email " +
            "ORDER BY rmci.roomMenuCartItemNum DESC")
    public Page<RoomMenuCartDetailDTO> findByCartDetailDTOList(@Param("email") String email, Pageable pageable);

    // 장바구니 안의 총 수량 반환 (member 기준)
    @Query("SELECT SUM(i.roomMenuCartItemAmount) FROM RoomMenuCartItem i WHERE i.roomMenuCart.member.memberEmail = :memberEmail")
    Integer getTotalItemCountByMemberEmail(@Param("memberEmail") String memberEmail);

    // RoomMenuCartItemRepository
    List<RoomMenuCartItem> findByRoomMenuCart(RoomMenuCart cart);

    Optional<RoomMenuCartItem> findByRoomMenuCartAndRoomMenu(RoomMenuCart cart, RoomMenu roomMenu);

}

