/***********************************************
 * 클래스명 : Storecartitem
 * 기능 :
 * 작성자 : 김예령
 * 작성일 : 2025-04-07
 * 수정 : 2025-04-25, 옵션 추가
 * ***********************************************/
package com.sixthsense.hexastay.entity;

import com.sixthsense.hexastay.dto.StoremenuOptionDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

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

    private Integer storecartitemCount;          //장바구니에 담은 수량

    private String storemenuOptions;  //주문한 상품의 옵션들??

    private Integer optionPrice;
}
