package com.sixthsense.hexastay.entity;
import com.sixthsense.hexastay.entity.base.BaseEntity;
import com.sixthsense.hexastay.enums.RoomMenuOrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class RoomMenuOrder extends BaseEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "roomMenuOrderNum")
    private Long roomMenuOrderNum;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member")  // 외래키 컬럼도 명확하게
    private Member member;

    @Enumerated(EnumType.STRING)
    private RoomMenuOrderStatus roomMenuOrderStatus;

    @Column(name = "regDate")
    private LocalDateTime regDate;

    @OneToMany(mappedBy = "roomMenuOrder", cascade = CascadeType.ALL)
    private List<RoomMenuOrderItem> orderItems = new ArrayList<>();
}
