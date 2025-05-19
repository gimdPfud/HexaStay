package com.sixthsense.hexastay.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettleDTO {
    private Long settleNum;
    private LocalDate settleDate;
    private Long settleSales;
    private Long settleCost;
    private Long settleProfit;
    private String settleStatus;
    private Long storeNum;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Long hotelRoomNum;
    private LocalDateTime createDate;
    private MemberDTO memberDTO;
    private HotelRoomDTO hotelRoomDTO;
    private StoreDTO storeDTO;
    private String menuNames;
    private Integer quantity;
    private Long unitPrice;
}
