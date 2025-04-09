package com.sixthsense.hexastay.dto;
import lombok.*;
import java.time.LocalDateTime;
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDTO {
    private Long reviewNum;                 //번호
    private String reviewTitle;             //제목
    private String reviewContent;           //내용
    private String reviewAuthor;           // 작성자
    private Integer reviewRating;           //별점
    private Integer reviewGood;             //좋아요
    private LocalDateTime createDate; //등록일자
    private LocalDateTime modifyDate; //수정일자
    private Long hotelRoomNum;      //방 참조
    private Long memberNum;            //회원 참조
}