/***********************************************
 * 인터페이스명 : OrderRoomRepository
 * 기능 :
 * 작성자 :
 * 작성일 : 2025-03-31
 * 수정 : 2025-03-31
 * ***********************************************/
package com.sixthsense.hexastay.repository;

import com.sixthsense.hexastay.entity.OrderRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderRoomRepository extends JpaRepository<OrderRoom, Long> {
    @Query("select a from OrderRoom a")
    public Page<OrderRoom> findAll(Pageable pageable);
}
