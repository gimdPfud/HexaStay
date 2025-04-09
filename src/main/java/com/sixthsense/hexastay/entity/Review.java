package com.sixthsense.hexastay.entity;
import com.sixthsense.hexastay.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reviewNum")
    private Long reviewNum;                 //번호
    @Column(name = "reviewTitle")
    private String reviewTitle;             //제목
    @Column(name = "reviewContent")
    private String reviewContent;           //내용
    @Column(name = "reviewRating")
    private Integer reviewRating;           //별점
    @Column(name = "reviewGood")
    private Integer reviewGood;             //좋아요
    @ManyToOne
    @JoinColumn(name = "hotelRoomNum")
    private HotelRoom hotelRoom;           //방 참조
    @ManyToOne
    @JoinColumn(name = "memberNum")
    private Member member;                  //회원 참조
}