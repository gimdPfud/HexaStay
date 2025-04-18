package com.sixthsense.hexastay.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

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

    private String erdSKU;

    private String erdType;

    private String erdUnit;

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
