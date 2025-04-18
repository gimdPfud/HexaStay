/***********************************************
 * 인터페이스명 : RoomMenuRepository
 * 기능 : 룸 메뉴 관련 데이터베이스 접근을 위한 JPA Repository 인터페이스
 * - RoomMenu 엔티티에 대한 기본적인 CRUD 연산 제공
 * - 특정 카테고리별 룸 메뉴 목록을 페이징 처리하여 조회하는 기능 제공
 * - 특정 카테고리 및 키워드를 포함하는 룸 메뉴 목록을 페이징 처리하여 조회하는 기능 제공 (정확히 일치)
 * - 특정 키워드를 포함하는 룸 메뉴 목록을 페이징 처리하여 조회하는 기능 제공
 * - 특정 가격 이하의 룸 메뉴 목록을 페이징 처리하여 조회하는 기능 제공
 * - 특정 재고량 초과의 룸 메뉴 목록을 페이징 처리하여 조회하는 기능 제공
 * - 특정 키워드를 포함하는 이름 또는 특정 가격 이하의 룸 메뉴 목록을 페이징 처리하여 조회하는 기능 제공
 * - 특정 호텔 룸 멤버의 이메일을 참조하여 룸 메뉴를 조회하는 기능 제공
 * - 특정 룸 메뉴의 좋아요 수를 증가시키는 기능 제공
 * - 특정 룸 메뉴의 싫어요 수를 감소시키는 기능 제공
 * 작성자 : 김윤겸
 * 작성일 : 2025-04-03
 * 수정일 : -
 * ***********************************************/

package com.sixthsense.hexastay.repository;
import com.sixthsense.hexastay.entity.RoomMenu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoomMenuRepository extends JpaRepository<RoomMenu, Long> {

    @Query("SELECT r FROM RoomMenu r " +
            "WHERE r.roomMenuName LIKE %:name% " +
            "AND (r.supportsMultilang = false OR r.approvedByDev = true)")
    Page<RoomMenu> searchByNameForUser(@Param("name") String name, Pageable pageable);

    @Query("SELECT r FROM RoomMenu r " +
            "WHERE r.roomMenuCategory = :category " +
            "AND r.roomMenuName LIKE %:name% " +
            "AND (r.supportsMultilang = false OR r.approvedByDev = true)")
    Page<RoomMenu> searchByCategoryAndNameForUser(@Param("category") String category,
                                                  @Param("name") String name,
                                                  Pageable pageable);

    @Query("SELECT r FROM RoomMenu r " +
            "WHERE r.roomMenuCategory = :category " +
            "AND (r.supportsMultilang = false OR r.approvedByDev = true)")
    Page<RoomMenu> searchByCategoryForUser(@Param("category") String category, Pageable pageable);

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


    // 이걸 기반으로 번역 승인 대기 리스트 뷰에서 필요한 데이터만 딱 걸러주는 매소드

    Page<RoomMenu> findByApprovedByDevFalseAndSupportsMultilangTrue(Pageable pageable);

    // 다국어 승인을 필터를 위해 추가한 레포지토리
    Page<RoomMenu> findBySupportsMultilangFalseOrApprovedByDevTrue(Pageable pageable);

    Page<RoomMenu> findByRoomMenuNameContainingAndSupportsMultilangFalseOrApprovedByDevTrue(String keyword, Pageable pageable);

    Page<RoomMenu> findByRoomMenuCategoryAndSupportsMultilangFalseOrApprovedByDevTrue(String category, Pageable pageable);

    Page<RoomMenu> findByRoomMenuCategoryAndRoomMenuNameContainingAndSupportsMultilangFalseOrApprovedByDevTrue(String category, String keyword, Pageable pageable);


}
