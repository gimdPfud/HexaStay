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

/***********************************************
 * 클래스명 : RoomMenuCartItemRepository
 * 기능 : 룸 메뉴 장바구니 아이템 관련 데이터베이스 접근을 위한 JPA Repository 인터페이스
 * - RoomMenuCartItem 엔티티에 대한 기본적인 CRUD 연산 제공
 * - 페이징 처리된 모든 장바구니 아이템 조회 기능 제공
 * - 특정 장바구니와 룸 메뉴를 기반으로 장바구니 아이템 조회 기능 제공
 * - 특정 회원의 장바구니 상세 정보를 DTO 형태로 조회하는 기능 제공 (페이징 적용)
 * - 특정 회원의 장바구니에 담긴 총 아이템 수량 조회 기능 제공
 * - 특정 장바구니에 속한 모든 장바구니 아이템 조회 기능 제공
 * 작성자 : 김윤겸
 * 작성일 : 2025-04-08
 * 수정일 : -
 * ***********************************************/

@Repository
public interface RoomMenuCartItemRepository extends JpaRepository<RoomMenuCartItem, Long> {

    @Query("select r from RoomMenuCartItem r")
    public Page<RoomMenuCartItem> findAll(Pageable pageable);


    // RoomMenuCart와 RoomMenu를 기반으로 RoomMenuCartItem을 찾는 메서드
    Optional<RoomMenuCartItem> findByRoomMenuCartAndRoomMenu(RoomMenuCart roomMenuCart, RoomMenu roomMenu);

    // 장바구니 리스트 fixme : 2025년 04.27일 오전 6시 이전으로 롤빽할수도 있음.
    @Query("SELECT new com.sixthsense.hexastay.dto.RoomMenuCartDetailDTO(" +
            "rmci.roomMenuCartItemNum, " +
            "rmi.roomMenuName, " +
            "rmi.roomMenuPrice, " +
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

//     @Query("SELECT new com.sixthsense.hexastay.dto.RoomMenuCartDetailDTO(" +
//            "rmci.roomMenuCartItemNum, rmi.roomMenuName, rmi.roomMenuPrice, rmci.roomMenuCartItemAmount, rmi.roomMenuImageMeta) " +
//            "FROM RoomMenuCartItem rmci " +
//            "JOIN RoomMenu rmi ON rmci.roomMenu.roomMenuNum = rmi.roomMenuNum " +
//            "WHERE rmci.roomMenuCart.member.memberEmail = :email " +
//            "ORDER BY rmci.roomMenuCartItemNum DESC")
//    public Page<RoomMenuCartDetailDTO> findByCartDetailDTOList(@Param("email") String email, Pageable pageable);

}

