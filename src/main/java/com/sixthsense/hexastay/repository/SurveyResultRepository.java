package com.sixthsense.hexastay.repository;

import com.sixthsense.hexastay.entity.Survey;
import com.sixthsense.hexastay.entity.SurveyResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SurveyResultRepository extends JpaRepository<SurveyResult, Long> {
    List<SurveyResult> findBySurvey_SurveyNum(Long surveyNum);
    boolean existsBySurvey_SurveyNumAndMemberEmail(Long surveyNum, String memberEmail);
}
