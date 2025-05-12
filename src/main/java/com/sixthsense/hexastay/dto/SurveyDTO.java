package com.sixthsense.hexastay.dto;

import com.sixthsense.hexastay.entity.Member;
import com.sixthsense.hexastay.entity.Room;
import com.sixthsense.hexastay.entity.Survey;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

public class SurveyDTO {

    private Long surveyResultNum;

    private Long surveyNum;
    private Long memberNum;
    private String memberEmail;
    private Room roomNum;

    private Integer surveyResultCleanliness;
    private Integer surveyResultStaff;
    private Integer surveyResultCheckInOut;
    private Integer surveyResultFacility;
    private Integer surveyResultFood;
    private Integer surveyResultValue;
    private String surveyResultComment;
    private Double surveyResultAverage;

}
