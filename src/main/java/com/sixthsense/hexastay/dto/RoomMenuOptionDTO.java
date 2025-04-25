package com.sixthsense.hexastay.dto;
import com.sixthsense.hexastay.entity.RoomMenu;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class RoomMenuOptionDTO {

    private Long roomMenuOptionNum; // pk

    private String roomMenuOptionName;

    private int roomMenuOptionPrice;

    private RoomMenu roomMenu;
}
