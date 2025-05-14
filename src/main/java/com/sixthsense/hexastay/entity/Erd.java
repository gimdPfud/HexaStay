package com.sixthsense.hexastay.entity;


import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@Entity
@Getter
@Setter
@ToString
@Table(name = "erd")
public class Erd {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "erdNum")
    private Long erdNum;

    private String erdName;

    private String erdSku;

    private String erdType;

    private Integer erdUnit;

    private String erdNote;

    private String erdPictureMeta;


    // FK 소속
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "companyNum")
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storeNum")
    private Store store;



}
