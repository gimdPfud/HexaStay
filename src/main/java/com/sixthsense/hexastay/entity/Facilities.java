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

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String facTitle;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String facContent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "companyNum")
    private Company company;
}