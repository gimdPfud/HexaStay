package com.sixthsense.hexastay.entity;

import com.sixthsense.hexastay.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.YearMonth;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "salaries")
public class Salaries extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long salNum;
    private Integer salBase;

    // 직책수당
    private String salPosition;

    // 직무수당
    private Integer salDuty;

    // 추가 인센
    private String salIncen;

    // 공제
    private String salDedu;

    // 날짜
    private YearMonth salDate;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin")
    private Admin admin;

}
