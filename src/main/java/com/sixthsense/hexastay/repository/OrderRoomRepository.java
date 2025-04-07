/***********************************************
 * 인터페이스명 : OrderRoomRepository
 * 기능 :
 * 작성자 :
 * 작성일 : 2025-03-31
 * 수정 : 2025-03-31
 * ***********************************************/
package com.sixthsense.hexastay.repository;

import com.sixthsense.hexastay.entity.OrderRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRoomRepository extends JpaRepository<OrderRoom, Long> {

}
