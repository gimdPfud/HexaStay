/***********************************************
 * 인터페이스명 : Storemenu
 * 기능 : 외부 업체의 처리를 담당하는 서비스 (인터페이스)
 * 작성자 : 김예령
 * 작성일 : 2025-04-01
 * 수정 : 2025-04-01
 * ***********************************************/
package com.sixthsense.hexastay.service;

import com.sixthsense.hexastay.dto.SurveyResultDTO;
import com.sixthsense.hexastay.entity.Survey;
import com.sixthsense.hexastay.entity.SurveyResult;

import java.time.LocalDate;
import java.util.List;

public interface SurveyService {
    Survey getSurveyById(Long id);
    Survey saveSurvey(Survey survey);
    void deleteSurvey(Long id);
    List<SurveyResult> getSurveyResults(Long surveyId);
    Survey getActiveSurvey();
    SurveyResult saveSurveyResult(SurveyResult surveyResult);
    boolean hasParticipated(Long surveyId, String memberEmail);
    // 체크아웃 날짜와 방번호를 포함한 설문 참여 여부 확인 (동일 고객이 여러번 숙박하는 경우 구분)
    boolean hasParticipatedWithCheckOutDate(String memberEmail, Long roomNum, LocalDate checkOutDate);
    void saveSurveyResult(SurveyResultDTO surveyResultDTO, String memberEmail, Long roomNum);
    
    // 회사별 설문조사 목록 조회
    List<Survey> getSurveysByCompany(Long companyNum);
    
    // 회사별 활성화된 설문조사 조회
    Survey getActiveSurveyByCompany(Long companyNum);
}
