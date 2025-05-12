package com.sixthsense.hexastay.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class SettleDTO {
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Long hotelRoomNum;
    private LocalDateTime createDate;
    private MemberDTO memberDTO;
    private HotelRoomDTO hotelRoomDTO;
    private StoreDTO storeDTO;
}
