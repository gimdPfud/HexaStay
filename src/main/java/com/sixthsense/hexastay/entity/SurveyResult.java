package com.sixthsense.hexastay.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SurveyResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long surveyResultNum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "surveyNum")
    private Survey survey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberNum")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roomNum")
    private Room room;

    private Integer surveyResultCleanliness;

    private Integer surveyResultStaff;

    private Integer surveyResultCheckInOut;

    private Integer surveyResultFacility;

    private Integer surveyResultFood;

    private Integer surveyResultValue;

    @Column(length = 1000)
    private String surveyResultSatisfaction;

    @Column(length = 1000)
    private String surveyResultImprovement;

    @Column(length = 1000)
    private String surveyResultComment;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime surveyResultSubmittedAt;

    private Double surveyResultAverage;

    @PrePersist
    @PreUpdate
    public void calculateAverageRating() {
        if (surveyResultCleanliness != null && surveyResultStaff != null && 
            surveyResultCheckInOut != null && surveyResultFacility != null && 
            surveyResultFood != null && surveyResultValue != null) {
            this.surveyResultAverage = (surveyResultCleanliness + surveyResultStaff + 
                surveyResultCheckInOut + surveyResultFacility + 
                surveyResultFood + surveyResultValue) / 6.0;
        }
    }
}
