/***********************************************
 * 인터페이스명 : CenterRepository
 * 기능 :
 * 작성자 :
 * 작성일 : 2025-03-31
 * 수정 : 2025-03-31
 * ***********************************************/
package com.sixthsense.hexastay.repository;

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
    Page<Center> findAll(Pageable pageable);

    // 회원가입용
    List<Center> findByCenterName (String centerName);

    //조직명으로 조회
    Page<Center> findByCenterNameContaining (String centerName, Pageable pageable);

    //브랜드명으로 조회
    Page<Center> findByCenterBrand (String keyword, Pageable pageable);
    Center findByCenterBrand(String keyword);

    //사업자번호로 조회
    Page<Center> findByCenterBusinessNum (String centerBusinessNum, Pageable pageable);


}
