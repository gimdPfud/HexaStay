/***********************************************
 * 인터페이스명 : ServiceUnionRepository
 * 기능 :
 * 작성자 :
 * 작성일 : 2025-03-31
 * 수정 : 2025-03-31
 * ***********************************************/
package com.sixthsense.hexastay.repository;

import com.sixthsense.hexastay.entity.ServiceUnion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ServiceUnionRepository extends JpaRepository<ServiceUnion, Long> {
    @Query("select a from ServiceUnion a")
    public Page<ServiceUnion> findAll(Pageable pageable);
}
