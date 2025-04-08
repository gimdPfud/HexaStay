/***********************************************
 * 인터페이스명 : BranchRepository
 * 기능 :
 * 작성자 :
 * 작성일 : 2025-03-31
 * 수정 : 2025-03-31
 * ***********************************************/
package com.sixthsense.hexastay.repository;

import com.sixthsense.hexastay.entity.Branch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {
    @Query("select a from Branch a")
    Page<Branch> findAll(Pageable pageable);

    List<Branch> findByCenter_CenterNum(Long centerNum);

    // 회원가입용
    List<Branch> findByBranchName(String branchName);
    List<Branch> findByCenter_CenterName (String centerName);

    //조직조회용
    Page<Branch> findByBranchNameContaining (String keyword, Pageable pageable);

    // 브랜드 조회용
    Page<Branch> findByCenter_CenterNum (Long centerNum, Pageable pageable);

}
