package com.sixthsense.hexastay.enums;

import lombok.Getter;

public enum CouponType {

    BIRTHDAY("BirthdayCoupon"),
    MEMBER("MemberCoupon"),
    SIGNUP("SignUpCoupon"),
    EVENT("EventCoupon");

    private final String code;

    CouponType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
