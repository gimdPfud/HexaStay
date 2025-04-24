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

public class RoomCare extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomCareNum; // 룸메뉴 케어 넘버 pk

    @ManyToOne
    private Member member; // 회원일 경우만

    @ManyToOne(optional = false)
    private HotelRoom hotelRoom; // 항상 필요

    private String roomCareRequestMessage; // 요구사항

    @Column(nullable = false)
    private Boolean isGuest; // 비회원 여부

}
