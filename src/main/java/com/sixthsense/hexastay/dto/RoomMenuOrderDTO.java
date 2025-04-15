package com.sixthsense.hexastay.dto;

import com.sixthsense.hexastay.entity.Member;
import com.sixthsense.hexastay.entity.RoomMenuOrderItem;
import com.sixthsense.hexastay.enums.RoomMenuOrderStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString

public class RoomMenuOrderDTO {

    private  Long roomMenuOrderNum; // pk

    private  int roomMenuOrderAmount; // 주문의 수량

    private Member member;

    private RoomMenuOrderStatus roomMenuOrderStatus;

    private LocalDateTime regDate;

    private List<RoomMenuOrderItemDTO> orderItemList = new ArrayList<>();

}
