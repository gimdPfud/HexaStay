/***********************************************
 * 인터페이스명 : RoomServiceRepository
 * 기능 :
 * 작성자 :
 * 작성일 : 2025-03-31
 * 수정 : 2025-03-31
 * ***********************************************/
package com.sixthsense.hexastay.repository;

import com.sixthsense.hexastay.dto.RoomMenuCartItemDTO;
import com.sixthsense.hexastay.entity.HotelRoom;
import com.sixthsense.hexastay.entity.RoomMenu;
import com.sixthsense.hexastay.entity.RoomMenuLike;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RoomMenuRepository extends JpaRepository<RoomMenu, Long> {

    /* 카테고리 별로 검색 */
    Page<RoomMenu> findByRoomMenuCategory(String category, Pageable pageable);

    // 카테고리와 이름이 정확히 일치하는 것만 검색
    Page<RoomMenu> findByRoomMenuCategoryAndRoomMenuNameContaining(String category, String keyword, Pageable pageable);

    // 검색기능
    Page<RoomMenu> findByRoomMenuNameContaining(String roomMenuName, Pageable pageable);

    /*가격별로 검색*/
    // RoomMenuRepository.java
    Page<RoomMenu> findByRoomMenuPriceLessThanEqual(Integer price, Pageable pageable);


    /*재고량으로 검색*/
    Page<RoomMenu> findByRoomMenuAmountGreaterThan(Integer price, Pageable pageable);

    /*이름 + 이름이나 가격으로 검색*/
    Page<RoomMenu> findByRoomMenuNameContainingOrRoomMenuPriceLessThanEqual(String keyword, Integer price, Pageable pageable);

    // 호텔 룸 멤버의 이메일 참조
    RoomMenu findByRoom_Member_MemberEmail(String memberEmail);

    // 좋아요
    @Modifying
    @Query("UPDATE RoomMenu rm SET rm.roomMenuDisLikes = rm.roomMenuDisLikes + 1 WHERE rm.roomMenuNum = :menuNum")
    void roomMenuIncrementLikes(@Param("menuNum") Long menuNum);

    // 싫어요
    @Modifying
    @Query("UPDATE RoomMenu rm SET rm.roomMenuDisLikes = rm.roomMenuDisLikes - 1 WHERE rm.roomMenuNum = :menuNum")
    void roomMenuDecrementDisLikes(@Param("menuNum") Long menuNum);

    Optional<RoomMenu> findByMember_MemberEmailAndRoomMenuNum(String memberEmail, Long roomMenuNum);





}
