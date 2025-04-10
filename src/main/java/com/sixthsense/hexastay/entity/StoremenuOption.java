/***********************************************
 * 클래스명 : StoremenuOption
 * 기능 : StoremenuOption 엔티티
 * 작성자 :
 * 작성일 : 2025-04-10
 * 수정 : 2025-04-10
 * ***********************************************/
package com.sixthsense.hexastay.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoremenuOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long storemenuOptionNum;        //옵션pk

    private String storemenuOptionName;     //옵션 이름
    private Integer storemenuOptionPrice;   //옵션 가격
    private String storemenuOptionStatus;   //활성화비활성화

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storemenu_num")
    private Storemenu storemenu;            //어느 메뉴의 옵션인지?? 연결
}