package com.sixthsense.hexastay.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor

public class RoomMenuLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomMenuLikeNum;

    @ManyToOne
    private Member member;

    @ManyToOne
    private RoomMenu roomMenu;

    private Boolean roomMenuLikedCheck; // true = 좋아요, false = 싫어요

}
