/***********************************************
 * 인터페이스명 : AdminRepository
 * 기능 :
 * 작성자 :
 * 작성일 : 2025-03-31
 * 수정 : 2025-03-31
 * ***********************************************/
package com.sixthsense.hexastay.repository;

import com.sixthsense.hexastay.dto.AdminDTO;
import com.sixthsense.hexastay.entity.Admin;
import com.sixthsense.hexastay.enums.AdminRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long>, AdminRepositoryCustom {
    @Query("select distinct a from Admin a left join fetch a.company left join fetch a.store")
    public Page<Admin> findAll(Pageable pageable);

    public Admin findByAdminNum(Long adminNum);
    public List<Admin> findByAdminActive(String active);

    // 컴퍼니 삭제용
    List<Admin> findByCompany_CompanyNum(Long companyNum);

    // 시큐리티용
    Admin findByAdminEmail(String adminEmail);


    // 월급입력시 사원 조회용
    List<Admin> findByCompany_CompanyNumAndAdminRoleIn(Long companyNum, List<String> adminRoles, Pageable pageable);
    List<Admin> findByStore_StoreNumAndAdminRoleIn(Long storeNum, List<String> adminRoles, Pageable pageable);

    // 월급 작성 직원 목록

    @Query("SELECT a FROM Admin a WHERE a.company.companyNum = :companyNum AND a.adminRole NOT IN ('exec', 'gm')")
    List<Admin> findBySalariesCompany(@Param("companyNum") Long companyNum);


    @Query("SELECT a FROM Admin a WHERE a.store.storeNum = :storeNum AND a.adminRole <> :excludedRole")
    List<Admin> findBySalariesStore(@Param("storeNum") Long storeNum);


}