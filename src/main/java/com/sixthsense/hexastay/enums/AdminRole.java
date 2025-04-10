package com.sixthsense.hexastay.enums;

import lombok.Getter;

@Getter
public enum AdminRole {
    EXEC("임원진"),
    HEAD("관리직"),
    CREW("사원"),
    SV("지사관리직"),
    PARTNER("협력업체"),
    MGR("운영총괄"),
    SUBMGR("부매니저"),
    STAFF("매장직원");

    private final String displayName;

    AdminRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static String displayNameFromCode(String code) {
        try {
            return AdminRole.valueOf(code.toUpperCase()).getDisplayName();
        } catch (Exception e) {
            return "알 수 없음";
        }
    }
}
