/***********************************************
 * 인터페이스명 : Storemenu
 * 기능 : 외부 업체의 처리를 담당하는 서비스 (인터페이스)
 * 작성자 : 김예령
 * 작성일 : 2025-04-01
 * 수정 : 2025-04-01
 * ***********************************************/
package com.sixthsense.hexastay.service;

import com.sixthsense.hexastay.dto.AdminDTO;
import com.sixthsense.hexastay.dto.StoreDTO;
import com.sixthsense.hexastay.entity.Member;
import com.sixthsense.hexastay.entity.Survey;
import com.sixthsense.hexastay.entity.SurveyResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public interface SurveyService {
    List<Survey> getAllSurveys();
    Survey getSurveyById(Long id);
    Survey saveSurvey(Survey survey);
    void deleteSurvey(Long id);
    List<SurveyResult> getSurveyResults(Long surveyId);
    Map<String, Double> getSurveyChartData(Long surveyId);
    Survey getActiveSurvey();
    SurveyResult saveSurveyResult(SurveyResult surveyResult);
}
