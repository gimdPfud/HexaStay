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
    // RoomMenuCartItemRepository.java (기존 코드에서 @Query 부분 수정)

    // 기존 findByCartDetailDTOList 메소드의 @Query 어노테이션 내부 JPQL 수정
    @Query("SELECT new com.sixthsense.hexastay.dto.RoomMenuCartDetailDTO(" +
            "rmci.roomMenuCartItemNum, " +                 // Long roomMenuCartDetailNum
            "rmi.roomMenuName, " +                         // String roomMenuCartDetailMenuItemName
            "rmi.roomMenuPrice, " +                        // Integer roomMenuCartDetailMenuItemPrice (상품의 개당 기본 가격)
            "rmci.roomMenuCartItemAmount, " +              // Integer roomMenuCartDetailMenuItemAmount
            "rmci.roomMenuSelectOptionName, " +            // String roomMenuSelectOptionName
            "rmci.roomMenuSelectOptionPrice, " +           // Integer roomMenuSelectOptionPrice (해당 아이템의 총 옵션 추가금액)
            "rmi.roomMenuImageMeta, " +                    // String roomMenuImageMeta
            "rmi.roomMenuNum, " +                          // ★ Long roomMenuId (실제 상품 RoomMenu의 PK)
            "rmci.roomMenuCartItemPrice" +                 // ★ Integer roomMenuCartItemPrice (장바구니 아이템의 최종 계산된 가격)
            ") " +
            "FROM RoomMenuCartItem rmci " +
            "JOIN rmci.roomMenu rmi " + // RoomMenu 엔티티를 rmi 별칭으로 조인
            "WHERE rmci.roomMenuCart.member.memberEmail = :email " +
            "ORDER BY rmci.roomMenuCartItemNum DESC")
    Page<RoomMenuCartDetailDTO> findByCartDetailDTOList(@Param("email") String email, Pageable pageable);
    // 장바구니 안의 총 수량 반환 (member 기준)
    @Query("SELECT SUM(i.roomMenuCartItemAmount) FROM RoomMenuCartItem i WHERE i.roomMenuCart.member.memberEmail = :memberEmail")
    Integer getTotalItemCountByMemberEmail(@Param("memberEmail") String memberEmail);

    // RoomMenuCartItemRepository
    List<RoomMenuCartItem> findByRoomMenuCart(RoomMenuCart cart);

    Optional<RoomMenuCartItem> findByRoomMenuCartAndRoomMenu(RoomMenuCart cart, RoomMenu roomMenu);

}

