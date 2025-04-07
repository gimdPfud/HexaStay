/***********************************************
 * 인터페이스명 : RoomServiceRepository
 * 기능 :
 * 작성자 :
 * 작성일 : 2025-03-31
 * 수정 : 2025-03-31
 * ***********************************************/
package com.sixthsense.hexastay.repository;

import com.sixthsense.hexastay.entity.RoomMenu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RoomMenuRepository extends JpaRepository<RoomMenu, Long> {
//    @Query("select a from RoomMenu a")
//    public Page<RoomMenu> findby(Pageable pageable);

    /* 카테고리 별로 검색 */
    Page<RoomMenu> findByRoomMenuCategory(String category, Pageable pageable);

    // 검색기능
    Page<RoomMenu> findByRoomMenuNameContaining(String roomMenuName, Pageable pageable);

    /*가격별로 검색*/
    Page<RoomMenu> findByRoomMenuPriceGreaterThan(Integer price, Pageable pageable);

    /*재고량으로 검색*/
    Page<RoomMenu> findByRoomMenuAmountGreaterThan(Integer price, Pageable pageable);

    /*이름 + 이름이나 가격으로 검색*/
    Page<RoomMenu> findByRoomMenuNameContainingOrRoomMenuPriceGreaterThan(String keyword, Integer price, Pageable pageable);

    /*아무거나 검색*/
    Page<RoomMenu> findByRoomMenuNameLikeOrRoomMenuPriceGreaterThanOrRoomMenuCategoryLikeOrRoomMenuAmountGreaterThanOrRoomMenuStatusLike(String roomMenuName, Integer roomMenuPrice, String roomMenuCategory, Integer roomMenuAmount, String roomMenuStatus, Pageable pageable);



    /*평점별로 검색*/


}
