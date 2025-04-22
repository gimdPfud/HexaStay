package com.sixthsense.hexastay.dto;

import com.sixthsense.hexastay.entity.Admin;
import com.sixthsense.hexastay.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.YearMonth;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalariesDTO {

    private Long salNum;
    private Integer salBase;
    private Integer salPosition;
    private Integer salDuty;
    private String salIncen;
    private String salDedu;
    private YearMonth salDate;
    private Admin admin;
}
