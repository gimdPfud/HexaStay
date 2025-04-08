/***********************************************
 * 클래스명 : Storecartitem
 * 기능 :
 * 작성자 :
 * 작성일 : 2025-04-07
 * 수정 : 2025-04-07
 * ***********************************************/
package com.sixthsense.hexastay.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Storecartitem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long storecartitemNum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storecart_num")
    private Storecart storecart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storemenu_num")
    private Storemenu storemenu;

    private int count;          //장바구니에 담은 수량
}
