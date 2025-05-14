/***********************************************
 * 클래스명 : Storecart
 * 기능 :
 * 작성자 :
 * 작성일 : 2025-04-07
 * 수정 : 2025-04-07
 * ***********************************************/
package com.sixthsense.hexastay.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Storecart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long storecartNum;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_num")
    private Room room;
}
