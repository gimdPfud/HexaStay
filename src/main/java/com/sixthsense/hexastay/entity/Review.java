/***********************************************
* 클래스명 : Review
* 기능 : Review 엔티티
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
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reviewNum")
    private Long reviewNum;
}