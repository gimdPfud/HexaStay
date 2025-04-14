package com.sixthsense.hexastay.dto;

import com.sixthsense.hexastay.entity.Member;
import com.sixthsense.hexastay.entity.RoomMenu;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class RoomMenuLikeDTO {

    private Long roomMenuLikeNum;

    private Member member;

    private RoomMenu roomMenu;

    private Boolean roomMenuLikedCheck; // true = 좋아요, false = 싫어요

}
