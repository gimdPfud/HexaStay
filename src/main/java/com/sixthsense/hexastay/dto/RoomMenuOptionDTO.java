package com.sixthsense.hexastay.dto;
import com.sixthsense.hexastay.entity.RoomMenu;
import lombok.*;

import java.util.Objects;

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

    private int roomMenuOptionStock;

    private Long roomMenuNum; // 룸메뉴의 관계키

    // 옵션의 중복을 막기위해 쓰이는 재정의문

    // === equals() 재정의 ===
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoomMenuOptionDTO that = (RoomMenuOptionDTO) o;
        return roomMenuOptionPrice == that.roomMenuOptionPrice &&
                roomMenuOptionStock == that.roomMenuOptionStock &&
                Objects.equals(roomMenuOptionName, that.roomMenuOptionName);
    }

    // === hashCode() 재정의 ===
    @Override
    public int hashCode() {
        return Objects.hash(roomMenuOptionName, roomMenuOptionPrice, roomMenuOptionStock);
    }
}
