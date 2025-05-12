package com.sixthsense.hexastay.repository;

import com.sixthsense.hexastay.entity.Survey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SurveyRepository extends JpaRepository<Survey, Long> {
    // 활성화된 설문조사 조회
    List<Survey> findBySurveyIsActiveTrue();
    
    // 활성화된 설문조사 중 하나만 조회 (최신순)
    Optional<Survey> findTopBySurveyIsActiveTrueOrderBySurveyNumDesc();
    
    // 회사별 설문조사 조회
    List<Survey> findByCompany_CompanyNum(Long companyNum);
}
