package com.sixthsense.hexastay.controller;

import com.sixthsense.hexastay.entity.SurveyResult;
import com.sixthsense.hexastay.service.SurveyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/surveyresult")
@RequiredArgsConstructor
public class SurveyResponseController {
    private final SurveyService surveyService;

    @PostMapping("/submit")
    public ResponseEntity<String> submitSurveyResponse(@RequestBody SurveyResult surveyResult) {
        try {
            surveyService.saveSurveyResult(surveyResult);
            return ResponseEntity.ok("설문조사 응답이 성공적으로 저장되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("설문조사 응답 저장 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
} 