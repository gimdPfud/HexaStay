package com.sixthsense.hexastay.dto;
import com.sixthsense.hexastay.entity.HotelRoom;
import com.sixthsense.hexastay.entity.Member;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor

public class RoomCareDTO {

    private Long roomCareNum; // 룸케어 넘버

    private Long hotelRoomNum; // 호텔 번호

    private Member member;

    private String roomCareRequestMessage; // 요구사항

    private Boolean isGuest;


}

