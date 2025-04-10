/***********************************************
 * 인터페이스명 : CenterRepository
 * 기능 :
 * 작성자 :
 * 작성일 : 2025-03-31
 * 수정 : 2025-03-31
 * ***********************************************/
package com.sixthsense.hexastay.repository;

import com.sixthsense.hexastay.dto.BranchDTO;
import com.sixthsense.hexastay.dto.CenterDTO;
import com.sixthsense.hexastay.entity.Branch;
import com.sixthsense.hexastay.entity.Center;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CenterRepository extends JpaRepository<Center, Long> {
    @Query("select a from Center a")
    public Page<Center> findAll(Pageable pageable);

    // 회원가입용
    public List<Center> findByCenterName (String centerName);

    //본사명으로 조회
    public Page<Center> findByCenterNameContaining (String centerName, Pageable pageable);

    //브랜드명으로 조회
    public Page<Center> findByCenterBrand (String keyword, Pageable pageable);
    public Center findByCenterBrand(String keyword);

    //본사 사업자등록번호로 조회
    public Page<Center> findByCenterBusinessNum (String centerBusinessNum, Pageable pageable);

    //브랜드명 중복없이 가져오기
    @Query("select distinct c.centerBrand from Center c")
    public List<String> findDistinctCenterBrand();

    //본사명 중복없이 가져오기
    @Query("select distinct c.centerName from Center c")
    public List<String> findDistinctCenterName();

}
