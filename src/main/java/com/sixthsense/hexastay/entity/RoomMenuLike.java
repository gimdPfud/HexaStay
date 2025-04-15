package com.sixthsense.hexastay.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor

public class RoomMenuLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomMenuLikeNum; // 좋아요의 pk

    private Boolean roomMenuLikedCheck; // true = 좋아요, false = 싫어요

    @ManyToOne
    private Member member;

    @ManyToOne
    private RoomMenu roomMenu;

}
