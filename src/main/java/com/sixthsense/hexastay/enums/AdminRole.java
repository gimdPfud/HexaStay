package com.sixthsense.hexastay.enums;

import lombok.Getter;

@Getter
public enum AdminRole {
    SUPERADMIN("슈퍼어드민"),
    //본사
    EXEC("임원진"),
    HEAD("매니저"),
    CREW("사원"),

    //지사, 지점
    GM("총괄"),
    SV("관리자"),
    AGENT("직원"),
    PARTNER("협력업체"),

    //스토어
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
