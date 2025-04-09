/***********************************************
 * 인터페이스명 : FacilityRepository
 * 기능 :
 * 작성자 :
 * 작성일 : 2025-03-31
 * 수정 : 2025-03-31
 * ***********************************************/
package com.sixthsense.hexastay.repository;

import com.sixthsense.hexastay.entity.Branch;
import com.sixthsense.hexastay.entity.Facility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FacilityRepository extends JpaRepository<Facility, Long> {
    @Query("select a from Facility a")
    public Page<Facility> findAll(Pageable pageable);

    // 회원가입용
    public List<Facility> findByFacilityName(String facilityName);
    public List<Facility> findByCenter_CenterName (String centerName);

    //조직 조회용
    public Page<Facility> findByFacilityNameContaining (String keyword, Pageable pageable);

    //브랜드 조회용
    public Page<Facility> findByCenter_CenterNum (Long centerNum, Pageable pageable);
    public List<Facility> findByCenter_CenterNum (Long centerNum);

    //지사 사업자등록번호로 조회
    public Page<Facility> findByCenter_CenterNameContaining (String keyword, Pageable pageable);

    //center 삭제 시 참조하고있는 facility 찾아서 삭제
    public void deleteByCenter_CenterNum (Long centerNum);


}
