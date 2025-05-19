/***********************************************
 * 인터페이스명 : UnionsRepository
 * 기능 :
 * 작성자 :
 * 작성일 : 2025-03-31
 * 수정 : 2025-03-31
 * ***********************************************/
package com.sixthsense.hexastay.repository;

import com.sixthsense.hexastay.entity.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StoreRepository extends JpaRepository<Store, Long>, StoreRepositoryCustom{
    @Query("select a from Store a where a.storeStatus = 'alive'")
    public Page<Store> findAll(Pageable pageable);

    @Query("select s from Store s where s.storeStatus = :status")
    public List<Store> findAll(String status);

    @Query("select s from Store s where s.storeStatus = 'alive'")
    public List<Store> findAll();

    @Query("select a from Store a where a.storeStatus='alive' and a.company.companyNum=:companyNum")
    List<Store> findByCompanyNum(Long companyNum);

//    @Query("select a from Store a where a.storeStatus='alive' and a.company.companyNum=:companyNum")
//    Page<Store> findByCompanyNum(Long companyNum, Pageable pageable);

    @Query("select a from Store a where a.storeStatus=:status and a.company.companyNum=:companyNum")
    List<Store> findByCompanyNum(Long companyNum, String status);

//    public Page<Store> findByStoreStatus(String status, Pageable pageable);

    @Query("select s from Store s where s.storeStatus = 'alive' and s.company.companyNum = :companyNum")
    List<Store> findByCompany_CompanyNum(Long companyNum);

}
