/***********************************************
 * 클래스명 : Facilities
 * 기능 : Facilities 엔티티
 * 작성자 : 김예령
 * 작성일 : 2025-04-29
 * 수정 : 2025-04-29
 * ***********************************************/
package com.sixthsense.hexastay.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Facilities {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long facilitiesNum;
    
    private String fsName;      //서비스 이름
    private String fsContent;   //서비스 설명
    private Integer fsPrice;    //서비스 가격
    private Integer fsAmountMax;   //서비스 수량(수용인원으로 응용??)
    private Integer fsAmount;   //서비스 수량(수용인원으로 응용??)
    private String fsStatus;    //서비스 상태(이용가능 불가능 삭제됨)
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "companyNum")
    private Company company;
}