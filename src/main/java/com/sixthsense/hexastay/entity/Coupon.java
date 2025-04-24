package com.sixthsense.hexastay.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long couponNum;

    @ManyToOne(optional = false)
    private Member member; // 멤버의 이메일을 조회해서 쿠폰 사용가능

    private String type; // 가입첫 쿠폰, 회원 상시쿠폰 2개

    private Integer discountRate; // 할인율

    private LocalDate issueDate; // 쿠폰 발급일자.

    private LocalDate expirationDate; // 쿠폰 만료일자.

    private boolean isGuest; // 비회원 여부

    private boolean used; // 사용여부 (단일용)

    private Integer repeatCouponCount; // 반복사용 쿠폰 (횟수)

    private LocalDateTime usedTime; // 사용한 날짜
}
