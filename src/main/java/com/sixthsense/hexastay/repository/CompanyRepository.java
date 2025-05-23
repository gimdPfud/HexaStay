/***********************************************
 * 인터페이스명 : CenterRepository
 * 기능 :
 * 작성자 :
 * 작성일 : 2025-03-31
 * 수정 : 2025-03-31
 * ***********************************************/
package com.sixthsense.hexastay.repository;

import com.sixthsense.hexastay.entity.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    @Query("select a from Company a")
    Page<Company> findAll(Pageable pageable);

    @Query("SELECT c FROM Company c " +
            "WHERE (:choice IS NULL OR c.companyType = :choice) " +
            "AND (c.companyNum = :companyNum OR c.companyParent = :companyNum)")
    Page<Company> findByCompanyNumOrParentCompanyNum(@Param("companyNum") Long companyNum,
                                                     @Param("choice") String choice,
                                                     Pageable pageable);

    @Query("SELECT c FROM Company c " +
            "WHERE (:choice = '' OR c.companyType = :choice) " +
            "AND ((:companyNum IS NULL) OR (c.companyNum = :companyNum OR c.companyParent = :companyNum)) " +
            "AND (" +
            "(:select = '전체' AND (:keyword = '' OR " +
            "    c.companyName LIKE %:keyword% OR " +
            "    c.companyBrand LIKE %:keyword% OR " +
            "    c.companyBusinessNum LIKE %:keyword%)) OR " +
            "(:select = 'company' AND c.companyName LIKE %:keyword%) OR " +
            "(:select = 'brandName' AND c.companyBrand LIKE %:keyword%) OR " +
            "(:select = 'businessNum' AND c.companyBusinessNum LIKE %:keyword%)" +
            ") " +
            "ORDER BY c.companyNum DESC")
    Page<Company> listSelectSearch(@Param("select") String select,
                                   @Param("choice") String choice,
                                   @Param("keyword") String keyword,
                                   @Param("companyNum") Long companyNum,
                                   Pageable pageable);

    List<Company> findByCompanyType(String companyType);

    List<Company> findByCompanyTypeAndCompanyParent(String companyType, Long companyParent);

    //활성화 비활성화 상태 조회
    List<Company> findByCompanyParent(Long companyParent);
    List<Company> findByCompanyNum(Long companyNum);

    //슈퍼어드민용
    @Query("SELECT c FROM Company c " +
            "WHERE (:choice IS NULL OR c.companyType = :choice)")
    Page<Company> findAllIgnoringCompanyNum(@Param("choice") String choice,
                                            Pageable pageable);

    List<Company> findByCompanyParentAndCompanyType(Long companyParent, String companyType);
}
