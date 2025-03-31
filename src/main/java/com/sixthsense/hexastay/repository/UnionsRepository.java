/***********************************************
 * 인터페이스명 : UnionsRepository
 * 기능 :
 * 작성자 :
 * 작성일 : 2025-03-31
 * 수정 : 2025-03-31
 * ***********************************************/
package com.sixthsense.hexastay.repository;

import com.sixthsense.hexastay.entity.Unions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UnionsRepository extends JpaRepository<Unions, Long> {
    @Query("select a from Unions a")
    public Page<Unions> findAll(Pageable pageable);
}
