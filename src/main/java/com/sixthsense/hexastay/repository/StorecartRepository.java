/***********************************************
 * 인터페이스명 : StorecartRepository
 * 기능 :
 * 작성자 :
 * 작성일 : 2025-04-08
 * 수정 : 2025-04-08
 * ***********************************************/
package com.sixthsense.hexastay.repository;

import com.sixthsense.hexastay.entity.Storecart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StorecartRepository extends JpaRepository<Storecart, Long> {
    Storecart findByRoom_RoomNum(Long roomNum);
}
