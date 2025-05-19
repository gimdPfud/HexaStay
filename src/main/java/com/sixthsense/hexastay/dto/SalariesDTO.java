package com.sixthsense.hexastay.dto;

import com.sixthsense.hexastay.entity.base.BaseEntity;
import lombok.*;

import java.time.LocalDateTime;
import java.time.YearMonth;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalariesDTO extends BaseEntity {
    private Long salNum;
    private Integer salBase;
    private String salPosition;
    private Integer salDuty;
    private String salIncen;
    private String salDedu;
    private YearMonth salDate;

    private LocalDateTime createDate;
    private LocalDateTime modifyDate;

    @Setter
    private AdminDTO adminDTO;

    private Long salariesNum;
    private Long adminNum;
    private String adminName;
    private Long storeNum;
    private String storeName;
    private int salariesBonus;
    private int salariesDeduction;
    private int salariesTotal;
    private LocalDateTime regDate;
    private LocalDateTime modDate;
}
