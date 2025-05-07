package com.sixthsense.hexastay.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RoomMenuCartItemOption")

public class RoomMenuCartItemOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomMenuCartItemOptionNum;

    private String roomMenuCartItemOptionName;
    private Integer roomMenuCartItemOptionPrice;
    private Integer roomMenuCartItemOptionAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roomMenuCartItem")
    private RoomMenuCartItem roomMenuCartItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roomMenuOption") // 실제 RoomMenuOption 엔티티의 PK를 참조하는 외래키 컬럼
    private RoomMenuOption roomMenuOption; // 옵션 마스터 데이터 참조


}
