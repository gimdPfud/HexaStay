/***********************************************
 * 클래스명 : FacilityDTO
 * 기능 : FacilityDTO 엔티티
 * 작성자 : 김예령
 * 작성일 : 2025-03-31
 * 수정 : 2025-03-31 BaseEntity 추가, 기존 날짜 필드 삭제 : 김예령
 * ***********************************************/
package com.sixthsense.hexastay.entity;

import com.sixthsense.hexastay.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Facility extends BaseEntity {
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "centerNum")
    private Center center;              //본사 참조
}