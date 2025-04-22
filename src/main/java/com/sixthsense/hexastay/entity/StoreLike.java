/***********************************************
 * 클래스명 : StoreLike
 * 기능 :
 * 작성자 :
 * 작성일 : 2025-04-22
 * 수정 : 2025-04-22
 * ***********************************************/
package com.sixthsense.hexastay.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
//@ToString
@NoArgsConstructor
public class StoreLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long storeLikeNum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberNum")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storeNum")
    private Store store;
}
