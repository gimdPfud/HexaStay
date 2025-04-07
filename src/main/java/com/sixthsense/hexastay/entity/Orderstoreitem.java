/***********************************************
* 클래스명 : Orderstoreitem
* 기능 : 
* 작성자 : 김예령
* 작성일 : 2025-04-07
* 수정 : 2025-04-07
* ***********************************************/
package com.sixthsense.hexastay.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Orderstoreitem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderstoreitemNum;     //pk

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderstore_num")
    private OrderStore orderStore;      //스토어주문 연결

    private Long orderstoreAmount;      //주문한 메뉴 양

    private Long orderstorePrice;       //주문한 메뉴 가격

    private Long orderstoreTotalPrice;  //주문한 메뉴들의 총가격
}
