package com.sixthsense.hexastay.dto;

import com.sixthsense.hexastay.entity.RoomMenu;
import com.sixthsense.hexastay.entity.RoomMenuTranslation;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor

public class RoomMenuTranslationDTO {

    private Long roomMenuTransLationNum;

    private String locale; // 'ko', 'en' 등

    private String roomMenuTransLationName;
    private String roomMenuTransLationContent;
    private String roomMenuTransLationCategory;
    private RoomMenu roomMenu;

    public static RoomMenuTranslationDTO fromEntity(RoomMenuTranslation entity) {
        RoomMenuTranslationDTO dto = new RoomMenuTranslationDTO();
        dto.setLocale(entity.getLocale());
        dto.setRoomMenuTransLationName(entity.getRoomMenuTransLationName());
        dto.setRoomMenuTransLationContent(entity.getRoomMenuTransLationContent());
        dto.setRoomMenuTransLationCategory(entity.getRoomMenuTransLationCategory());
        return dto;
    }

}
