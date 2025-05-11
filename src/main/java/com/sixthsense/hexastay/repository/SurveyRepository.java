package com.sixthsense.hexastay.repository;

import com.sixthsense.hexastay.entity.Survey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SurveyRepository extends JpaRepository<Survey, Long> {
    // 활성화된 설문조사 조회
    List<Survey> findBySurveyIsActiveTrue();
}
