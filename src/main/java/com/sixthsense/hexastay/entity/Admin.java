/***********************************************
 * 클래스명 : Admin
 * 기능 : Admin 엔티티
 * 작성자 : 깁부환
 * 작성일 : 2025-03-31
 * 수정 : 2025-03-31 BaseEntity 추가, 기존 날짜 필드 삭제 : 김예령
 * ***********************************************/
package com.sixthsense.hexastay.entity;

import com.sixthsense.hexastay.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@Entity
@Getter
@Setter
@ToString
@Table(name = "admin")
public class Admin extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "adminNum")
    private Long adminNum;

    //어드민 사번
    @Column(unique = true, nullable = false)
    private String adminEmployeeNum;

    //어드민 패스워드
    private String adminPassword;

    //어드민 이름
    private String adminName;

    //어드민 이메일
    @Column(unique = true, nullable = false)
    private String adminEmail;

    //어드민 주소
    private String adminAddress;

    //어드민 주민번호
    @Column(unique = true, nullable = false)
    private String adminResidentNum;

    //어드민 전화번호
    private String adminPhone;

    //어드민 직급
    private String adminRole;

    //어드민 승인여부
    private String adminActive;

    //어드민 직책
    private String adminPosition;

    //어드민 입사일
    private LocalDate adminJoinDate;

    // 어드민 사진
    private String adminProfileMeta;

    // FK 소속
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "companyNum")
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storeNum")
    private Store store;

}