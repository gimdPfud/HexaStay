package com.sixthsense.hexastay.repository;

import com.sixthsense.hexastay.entity.SurveyResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface SurveyResultRepository extends JpaRepository<SurveyResult, Long> {
    List<SurveyResult> findBySurvey_SurveyNum(Long surveyNum);
    boolean existsBySurvey_SurveyNumAndMemberEmail(Long surveyNum, String memberEmail);
    
    // 회사별, 제출일시 범위로 설문 결과 조회
    List<SurveyResult> findBySurvey_Company_CompanyNumAndSurveyResultSubmittedAtBetween(
            Long companyNum, LocalDateTime startDate, LocalDateTime endDate);
            
    // 이메일, 체크아웃 날짜, 방번호로 설문 참여 여부 조회(같은 고객이 같은 방에 다른 기간에 머무른 경우 구분)
    @Query("SELECT COUNT(sr) > 0 FROM SurveyResult sr " +
           "WHERE sr.memberEmail = :memberEmail " +
           "AND sr.room.roomNum = :roomNum " +
           "AND CAST(sr.room.checkOutDate AS LocalDate) = :checkOutDate")
    boolean existsByMemberEmailAndRoomNumAndCheckOutDate(
            @Param("memberEmail") String memberEmail, 
            @Param("roomNum") Long roomNum,
            @Param("checkOutDate") LocalDate checkOutDate);
}
