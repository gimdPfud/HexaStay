package com.sixthsense.hexastay.enums;

import lombok.Getter;

@Getter
public enum CompanyType {
    CENTER("본사"),
    BRANCH("지사"),
    FACILITY("지점");

    private final String displayName;

    CompanyType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static String displayNameFromCode(String code) {
        try {
            return CompanyType.valueOf(code.toUpperCase()).getDisplayName();
        } catch (Exception e) {
            return "알 수 없음";
        }
    }
}
