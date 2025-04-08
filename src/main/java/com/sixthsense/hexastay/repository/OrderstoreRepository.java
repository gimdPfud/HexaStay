/***********************************************
 * 인터페이스명 : OrderstoreRepository
 * 기능 :
 * 작성자 :
 * 작성일 : 2025-03-31
 * 수정 : 2025-03-31
 * ***********************************************/
package com.sixthsense.hexastay.repository;

import com.sixthsense.hexastay.entity.Orderstore;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderstoreRepository extends JpaRepository<Orderstore, Long> {
    @Query("select a from Orderstore a")
    public Page<Orderstore> findAll(Pageable pageable);

}