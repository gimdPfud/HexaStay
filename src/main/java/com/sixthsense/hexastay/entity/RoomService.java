/***********************************************
 * 클래스명 : RoomServiceDTO
 * 기능 : RoomServiceDTO 엔티티
 * 작성자 :
 * 작성일 : 2025-03-31
 * 수정 : 2025-03-31
 * ***********************************************/
package com.sixthsense.hexastay.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "roomService")
public class RoomService {
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

    @CreatedDate
    @Column(name = "roomServiceCreateDate")
    private LocalDateTime roomServiceCreateDate;    //등록일자

    @LastModifiedDate
    @Column(name = "roomServiceModifyDate")
    private LocalDateTime roomServiceModifyDate;    //수정일자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotelRoomNum")
    private HotelRoom roomServiceHotelRoom;         //방 참조
}