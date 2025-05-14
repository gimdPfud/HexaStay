package com.sixthsense.hexastay.repository;

import com.sixthsense.hexastay.entity.Salaries;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SalariesRepository extends JpaRepository <Salaries, Long> {

    // 본사 계열
    @Query("SELECT DISTINCT s FROM Salaries s " +
            "JOIN FETCH s.admin a " +
            "LEFT JOIN FETCH a.company " +
            "LEFT JOIN FETCH a.store " +
            "WHERE a.company.companyNum = :companyNum " +
            "AND (a.adminEmail = :email OR " +
            "     (a.adminRole = 'crew' AND :role IN ('exec', 'head')))")
    List<Salaries> findHeadOfficeSalaries(@Param("email") String email,
                                          @Param("role") String role,
                                          @Param("companyNum") Long companyNum);


    // 지사/지점 계열
    @Query("SELECT DISTINCT s FROM Salaries s " +
            "JOIN FETCH s.admin a " +
            "LEFT JOIN FETCH a.company " +
            "LEFT JOIN FETCH a.store " +
            "WHERE a.company.companyNum = :companyNum " +
            "AND (a.adminEmail = :email OR " +
            "     (:role = 'gm' AND a.adminRole IN ('sv', 'agent', 'partner')) OR " +
            "     (:role = 'sv' AND a.adminRole = 'agent'))")
    List<Salaries> findBranchSalaries(@Param("email") String email,
                                      @Param("role") String role,
                                      @Param("companyNum") Long companyNum);


    // 스토어 계열
    @Query("SELECT DISTINCT s FROM Salaries s " +
            "JOIN FETCH s.admin a " +
            "LEFT JOIN FETCH a.company " +
            "LEFT JOIN FETCH a.store " +
            "WHERE a.store.storeNum = :storeNum " +
            "AND (a.adminEmail = :email OR " +
            "     (:role = 'mgr' AND a.adminRole IN ('submgr', 'staff')) OR " +
            "     (:role = 'submgr' AND a.adminRole = 'staff'))")
    List<Salaries> findStoreSalaries(@Param("email") String email,
                                     @Param("role") String role,
                                     @Param("storeNum") Long storeNum);

}
