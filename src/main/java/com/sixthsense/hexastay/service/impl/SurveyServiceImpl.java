package com.sixthsense.hexastay.service.impl;

import com.sixthsense.hexastay.entity.Survey;
import com.sixthsense.hexastay.entity.SurveyResult;
import com.sixthsense.hexastay.repository.SurveyRepository;
import com.sixthsense.hexastay.repository.SurveyResultRepository;
import com.sixthsense.hexastay.service.SurveyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class SurveyServiceImpl implements SurveyService {
    private final SurveyRepository surveyRepository;
    private final SurveyResultRepository surveyResultRepository;

    @Override
    public List<Survey> getAllSurveys() {
        return surveyRepository.findAll();
    }

    @Override
    public Survey getSurveyById(Long id) {
        return surveyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("설문조사를 찾을 수 없습니다."));
    }

    @Override
    public Survey saveSurvey(Survey survey) {
        return surveyRepository.save(survey);
    }

    @Override
    public void deleteSurvey(Long id) {
        surveyRepository.deleteById(id);
    }

    @Override
    public List<SurveyResult> getSurveyResults(Long surveyId) {
        return surveyResultRepository.findBySurvey_SurveyNum(surveyId);
    }

    @Override
    public Map<String, Double> getSurveyChartData(Long surveyId) {
        List<SurveyResult> results = getSurveyResults(surveyId);
        Map<String, Double> chartData = new HashMap<>();
        
        if (!results.isEmpty()) {
            double avgCleanliness = results.stream()
                    .mapToDouble(SurveyResult::getSurveyResultCleanliness)
                    .average()
                    .orElse(0.0);
            
            double avgStaffRating = results.stream()
                    .mapToDouble(SurveyResult::getSurveyResultStaff)
                    .average()
                    .orElse(0.0);
            
            double avgCheckInOut = results.stream()
                    .mapToDouble(SurveyResult::getSurveyResultCheckInOut)
                    .average()
                    .orElse(0.0);
            
            chartData.put("청결도", avgCleanliness);
            chartData.put("직원 친절도", avgStaffRating);
            chartData.put("체크인/아웃", avgCheckInOut);
        }
        
        return chartData;
    }

    @Override
    public Survey getActiveSurvey() {
        return surveyRepository.findBySurveyIsActiveTrue()
                .stream()
                .findFirst()
                .orElse(null);
    }

    @Override
    public SurveyResult saveSurveyResult(SurveyResult surveyResult) {
        // 평균 점수 계산
        surveyResult.calculateAverageRating();
        return surveyResultRepository.save(surveyResult);
    }
}
