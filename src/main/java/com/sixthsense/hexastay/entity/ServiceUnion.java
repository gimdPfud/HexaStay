/***********************************************
 * 클래스명 : ServiceUnion
 * 기능 : ServiceUnion 엔티티
 * 작성자 :
 * 작성일 : 2025-03-31
 * 수정 : 2025-03-31
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
public class ServiceUnion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "serviceUnionNum")
    private Long serviceUnionNum;
}