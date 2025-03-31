/***********************************************
 * 인터페이스명 : OrderServiceRepository
 * 기능 :
 * 작성자 :
 * 작성일 : 2025-03-31
 * 수정 : 2025-03-31
 * ***********************************************/
package com.sixthsense.hexastay.repository;

import com.sixthsense.hexastay.entity.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderServiceRepository extends JpaRepository<OrderService, Long> {
    @Query("select a from OrderService a")
    public Page<OrderService> findAll(Pageable pageable);
}
