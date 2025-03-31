/***********************************************
 * 클래스명 : Unions
 * 기능 : Unions 엔티티
 * 작성자 : 김예령
 * 작성일 : 2025-03-31
 * 수정 : 2025-03-31, Union이 예약어라서 변경함
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
public class Unions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "unionsNum")
    private Long unionsNum;
}