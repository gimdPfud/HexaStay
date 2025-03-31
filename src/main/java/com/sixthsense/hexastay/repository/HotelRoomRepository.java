/***********************************************
 * 인터페이스명 : HotelRoomRepository
 * 기능 :
 * 작성자 :
 * 작성일 : 2025-03-31
 * 수정 : 2025-03-31
 * ***********************************************/
package com.sixthsense.hexastay.repository;

import com.sixthsense.hexastay.entity.HotelRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface HotelRoomRepository extends JpaRepository<HotelRoom, Long> {
    @Query("select a from HotelRoom a")
    public Page<HotelRoom> findAll(Pageable pageable);
}
