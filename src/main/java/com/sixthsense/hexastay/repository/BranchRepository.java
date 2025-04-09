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
    public Page<Branch> findAll(Pageable pageable);

    public List<Branch> findByCenter_CenterNum(Long centerNum);

    // 회원가입용
    public List<Branch> findByBranchName(String branchName);
    public List<Branch> findByCenter_CenterName (String centerName);

    //조직조회용
    public Page<Branch> findByBranchNameContaining (String keyword, Pageable pageable);

    // 브랜드 조회용
    public Page<Branch> findByCenter_CenterNum (Long centerNum, Pageable pageable);

    //center 삭제 시 참조하고있는 branch 찾아서 삭제
    public void deleteByCenter_CenterNum (Long centerNum);


}
