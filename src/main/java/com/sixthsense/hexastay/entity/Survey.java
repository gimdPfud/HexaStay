package com.sixthsense.hexastay.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Survey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long surveyNum;

    @Column(nullable = false)
    private String surveyTitle;

    @Column(columnDefinition = "TEXT")
    private String surveyContent;

    @Column(columnDefinition = "LONGTEXT")
    private String surveyFormHtml;

    private boolean surveyIsActive = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime surveyCreatedAt;

    @UpdateTimestamp
    private LocalDateTime surveyUpdatedAt;

    private String surveyCreatedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "companyNum")
    private Company company;
}
