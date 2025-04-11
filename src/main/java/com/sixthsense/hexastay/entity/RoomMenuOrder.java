package com.sixthsense.hexastay.entity;
import com.sixthsense.hexastay.entity.base.BaseEntity;
import com.sixthsense.hexastay.enums.RoomMenuOrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class RoomMenuOrder extends BaseEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "roomMenuOrder")
    private Long roomMenuOrder; // 주문의 pk

    @ManyToOne(fetch = FetchType.LAZY)  //다대일 멤버를 참조
    @JoinColumn(name = "member")
    private Member member;

    //주문상태 //주문  주문취소

    @Enumerated(EnumType.STRING)
    private RoomMenuOrderStatus roomMenuOrderStatus;

    @OneToMany(mappedBy = "roomMenuOrder", cascade = CascadeType.ALL)
    //연관관계의 주인인 (자식객체 Foreign Key
    //달아준 아이) 테이블에서 참조하는 부모의
    //클래스명과 변수명중 변수
    private List<RoomMenuOrderItem> orderItems = new ArrayList<>();





}
