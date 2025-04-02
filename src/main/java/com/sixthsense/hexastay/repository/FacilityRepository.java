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
}
