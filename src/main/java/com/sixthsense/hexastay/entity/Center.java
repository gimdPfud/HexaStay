/***********************************************
 * 클래스명 : Center
 * 기능 : Center 엔티티
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
public class Center {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "centerNum")
    private Long centerNum;
}