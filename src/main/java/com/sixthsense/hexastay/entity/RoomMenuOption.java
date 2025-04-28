package com.sixthsense.hexastay.entity;

import com.sixthsense.hexastay.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor

public class RoomMenuOption extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomMenuOptionNum; // pk

    // 옵션 이름 (예: 치즈 추가, 곱빼기 등)
    @Column(nullable = false)
    private String roomMenuOptionName;

    // 옵션 가격 (기본 단위는 원, 음수도 허용 가능)
    @Column(nullable = false)
    private int roomMenuOptionPrice;

    private int roomMenuOptionStock;

    // 옵션이 어떤 메뉴에 속하는지
    @ManyToOne
    @JoinColumn(name = "roomMenu")
    private RoomMenu roomMenu;




}
