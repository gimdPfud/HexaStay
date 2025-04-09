package com.sixthsense.hexastay.dto;
import lombok.*;
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewLikeDTO {
    private Long reviewLikeNum;
    private Long reviewNum;
    private Long memberNum;
}
