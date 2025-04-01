/***********************************************
 * 인터페이스명 : StoreMenuRepository
 * 기능 :
 * 작성자 : 김예령
 * 작성일 : 2025-03-31
 * 수정 : 2025-04-01 이름 변경 : 김예령
 * ***********************************************/
package com.sixthsense.hexastay.repository;

import com.sixthsense.hexastay.entity.StoreMenu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StoreMenuRepository extends JpaRepository<StoreMenu, Long> {
    @Query("select a from StoreMenu a")
    public Page<StoreMenu> findAll(Pageable pageable);
}
