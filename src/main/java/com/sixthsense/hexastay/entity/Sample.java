/***********************************************
 * 클래스명 : Sample
 * 기능 : 샘플 엔티티입니다! 만들긴 했지만, 나중에 얼마든지 바뀔 수도 있는 형식입니다.
 * 작성자 : 김예령
 * 작성일 : 2025-03-26
 * 수정 : 2025-03-26 엔티티생성,김예령
 * ***********************************************/
package com.sixthsense.hexastay.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "sample")
@EntityListeners(AuditingEntityListener.class)
@SequenceGenerator(
        name = "sample_seq",
        sequenceName = "sample_seq",
        initialValue = 1,
        allocationSize = 1
)
public class Sample {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sample_seq")
    @Column(name = "sampleNum")
    private Long sampleNum;

    private String sampleColumn;

    @CreatedDate
    @Column(updatable = false, nullable = false, name = "sampleRegDate")
    private LocalDateTime sampleRegDate;
}
