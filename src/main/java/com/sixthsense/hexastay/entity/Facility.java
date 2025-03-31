/***********************************************
 * 클래스명 : FacilityDTO
 * 기능 : FacilityDTO 엔티티
 * 작성자 : 김예령
 * 작성일 : 2025-03-31
 * 수정 : 2025-03-31
 * ***********************************************/
package com.sixthsense.hexastay.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Facility {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "facilityNum")
    private Long facilityNum;

    @Column(name = "facilityName")
    private String facilityName;                //이름

    @Column(name = "facilityPhone")
    private String facilityPhone;               //전화번호

    @Column(name = "facilityEmail")
    private String facilityEmail;               //이메일

    @Column(name = "facilityAddress")
    private String facilityAddress;             //주소

    @Column(name = "facilityCeoName")
    private String facilityCeoName;             //대표 이름

    @CreatedDate
    @Column(name = "facilityCreateDate")
    private LocalDateTime facilityCreateDate;   //등록일자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "centerNum")
    private Center facilityCenter;              //본사 참조
}