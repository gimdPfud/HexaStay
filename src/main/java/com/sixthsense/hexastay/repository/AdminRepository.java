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

public interface AdminRepository extends JpaRepository<Admin, Long>{
    @Query("select distinct a from Admin a left join fetch a.company left join fetch a.store")
    public Page<Admin> findAll(Pageable pageable);

    public Admin findByAdminNum(Long adminNum);
    @Query("SELECT a FROM Admin a WHERE a.adminActive = :active")
    public List<Admin> findByAdminActive(@Param("active") String active);

    // 리스트
    List<Admin> findByCompany_CompanyNum(Long companyNum);

    Page<Admin> findByCompany_CompanyNum(Long companyNum, Pageable pageable);
    Page<Admin> findByStore_StoreNum(Long storeNum, Pageable pageable);

    // 리스트 서치
    @Query("SELECT a FROM Admin a " +
           "WHERE a.company.companyNum = :companyNum " +
           "AND (" +
           "(:type = 'adminName' AND a.adminName LIKE CONCAT('%', :keyword, '%')) OR " +
           "(:type = 'adminEmployeeNum' AND a.adminEmployeeNum LIKE CONCAT('%', :keyword, '%')) OR " +
           "(:type = 'adminRole' AND a.adminRole LIKE CONCAT('%', :keyword, '%')) OR " +
           "(:type = 'adminPosition' AND a.adminPosition LIKE CONCAT('%', :keyword, '%'))" +
           ")")
    Page<Admin> listPageAdminSearch(@Param("companyNum") Long companyNum,
                                   @Param("type") String type,
                                   @Param("keyword") String keyword,
                                   Pageable pageable);

    @Query("SELECT a FROM Admin a " +
           "WHERE a.store.storeNum = :storeNum " +
           "AND (" +
           "(:type = 'adminName' AND a.adminName LIKE CONCAT('%', :keyword, '%')) OR " +
           "(:type = 'adminEmployeeNum' AND a.adminEmployeeNum LIKE CONCAT('%', :keyword, '%')) OR " +
           "(:type = 'adminRole' AND a.adminRole LIKE CONCAT('%', :keyword, '%')) OR " +
           "(:type = 'adminPosition' AND a.adminPosition LIKE CONCAT('%', :keyword, '%'))" +
           ")")
    Page<Admin> listPageAdminStoreSearch(@Param("storeNum") Long storeNum,
                                        @Param("type") String type,
                                        @Param("keyword") String keyword,
                                        Pageable pageable);

    // 시큐리티용
    Admin findByAdminEmail(String adminEmail);

    // 월급 작성 직원 목록
    @Query("SELECT a FROM Admin a WHERE a.company.companyNum = :companyNum AND a.adminRole NOT IN ('exec', 'gm')")
    List<Admin> findBySalariesCompany(@Param("companyNum") Long companyNum);

    @Query("SELECT a FROM Admin a WHERE a.store.storeNum = :storeNum AND a.adminRole <> :excludedRole")
    List<Admin> findBySalariesStore(@Param("storeNum") Long storeNum);


    // 사번 생성용
    @Query("SELECT MAX(a.adminEmployeeNum) FROM Admin a WHERE a.adminEmployeeNum LIKE :prefix")
    String findMaxEmpNumStartingWith(@Param("prefix") String prefix);

    // 본인 확인용
    Admin findByAdminNameAndAdminEmployeeNumAndAdminResidentNumStartingWith(
        String adminName, 
        String adminEmployeeNum, 
        String adminResidentNum
    );
    
    // 슈퍼어드민용 전체 직원 검색
    @Query("SELECT a FROM Admin a " +
           "WHERE (" +
           "(:type = 'adminName' AND a.adminName LIKE CONCAT('%', :keyword, '%')) OR " +
           "(:type = 'adminEmployeeNum' AND a.adminEmployeeNum LIKE CONCAT('%', :keyword, '%')) OR " +
           "(:type = 'adminRole' AND a.adminRole LIKE CONCAT('%', :keyword, '%')) OR " +
           "(:type = 'adminPosition' AND a.adminPosition LIKE CONCAT('%', :keyword, '%'))" +
           ")")
    Page<Admin> searchAllAdmins(@Param("type") String type,
                               @Param("keyword") String keyword,
                               Pageable pageable);
}
