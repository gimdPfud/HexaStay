package com.sixthsense.hexastay.repository;

import com.sixthsense.hexastay.entity.Survey;
import com.sixthsense.hexastay.entity.SurveyResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SurveyResultRepository extends JpaRepository<SurveyResult, Long> {
    List<SurveyResult> findBySurvey_SurveyNum(Long surveyNum);
    boolean existsBySurvey_SurveyNumAndMemberEmail(Long surveyNum, String memberEmail);
    
    // 회사별, 제출일시 범위로 설문 결과 조회
    List<SurveyResult> findBySurvey_Company_CompanyNumAndSurveyResultSubmittedAtBetween(
            Long companyNum, LocalDateTime startDate, LocalDateTime endDate);
}
