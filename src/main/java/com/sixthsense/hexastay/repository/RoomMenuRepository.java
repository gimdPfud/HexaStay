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

    Page<RoomMenu> findByRoomMenuCategory(String category, Pageable pageable);

    // 검색기능
    Page<RoomMenu> findByRoomMenuNameContaining(String roomMenuName, Pageable pageable);
}
