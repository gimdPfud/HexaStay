package com.sixthsense.hexastay.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor

public class RoomMenuTranslation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomMenuTransLationNum;

    private String locale; // 'ko', 'en' 등

    private String roomMenuTransLationName;
    private String roomMenuTransLationContent;
    private String roomMenuTransLationCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_menu_id")  // 이걸로 바꿈
    private RoomMenu roomMenu;

}
