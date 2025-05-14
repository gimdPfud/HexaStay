/***********************************************
 * 인터페이스명 : OrderstoreitemRepository
 * 기능 :
 * 작성자 :
 * 작성일 : 2025-04-08
 * 수정 : 2025-04-08
 * ***********************************************/
package com.sixthsense.hexastay.repository;

import com.sixthsense.hexastay.entity.Orderstoreitem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderstoreitemRepository extends JpaRepository<Orderstoreitem, Long> {
    @Query("select a from Orderstoreitem a")
    public Page<Orderstoreitem> findAll(Pageable pageable);
}
