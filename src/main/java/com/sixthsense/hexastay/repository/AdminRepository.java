/***********************************************
 * 인터페이스명 : AdminRepository
 * 기능 :
 * 작성자 :
 * 작성일 : 2025-03-31
 * 수정 : 2025-03-31
 * ***********************************************/
package com.sixthsense.hexastay.repository;

import com.sixthsense.hexastay.entity.Admin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    @Query("select a from Admin a")
    public Page<Admin> findAll(Pageable pageable);

    public Admin findByAdminNum(Long adminNum);
    public List<Admin> findByAdminActive(Boolean active);


    // 시큐리티용
    Admin findByAdminEmail(String adminEmail);


    //FK
    @Query("""
    select a from Admin a
    left join fetch a.center
    left join fetch a.branch
    left join fetch a.facility
    left join fetch a.store
    """)
    Page<Admin> findAllWithJoins(Pageable pageable);

    //FK 순환 조회용
//    @Query("select a from Admin a join fetch a.center c")
//    Page<Admin> findAllWithCenter(Pageable pageable);
//
//    @Query("select a from Admin a join fetch a.branch b")
//    Page<Admin> findAllWithBranch(Pageable pageable);
//
//    @Query("select a from Admin a join fetch a.facility f")
//    Page<Admin> findAllWithFacility(Pageable pageable);
//
//    @Query("select a from Admin a join fetch a.store s")
//    Page<Admin> findAllWithStore(Pageable pageable);

}
