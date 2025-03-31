/***********************************************
 * 클래스명 : RoomServiceDTO
 * 기능 : RoomServiceDTO 엔티티
 * 작성자 : 김예령
 * 작성일 : 2025-03-31
 * 수정 : 2025-03-31 BaseEntity 추가, 기존 날짜 필드 삭제 : 김예령
 * ***********************************************/
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
@Table(name = "roomService")
public class RoomService extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "roomServiceNum")
    private Long roomServiceNum;

    @Column(name = "roomServiceName")
    private String roomServiceName;                 //이름

    @Column(name = "roomServicePrice")
    private Integer roomServicePrice;                //가격

    @Column(name = "roomServiceContent")
    private String roomServiceContent;              //상품설명

    @Column(name = "roomServiceCategory")
    private String roomServiceCategory;             //카테고리

    @Column(name = "roomServiceStatus")
    private boolean roomServiceStatus;              //활성화 여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotelRoomNum")
    private HotelRoom hotelRoom;                    //방 참조
}