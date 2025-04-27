package com.sixthsense.hexastay.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RoomMenuCartItemOption")

public class RoomMenuCartItemOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rooMenuCartItemOptionNum;

    private String roomMenuCartItemOptionName;
    private Integer roomMenuCartItemOptionPrice;
    private Integer roomMenuCartItemOptionAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roomMenuCartItem")
    private RoomMenuCartItem roomMenuCartItem;


}
