package com.sixthsense.hexastay.dto;

/********************************************
 * 클래스명 : AdminDTO
 * 기능 : AdminDTO
 * 작성자 : 김부환
 * 작성일 : 2025-03-31
 * 수정 : 2025-03-31
 * ***********************************************/


import com.sixthsense.hexastay.enums.AdminRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminDTO {


    private Long adminNum;

    //어드민 사번
    private String adminEmployeeNum;

    //어드민 패스워드
    private String adminPassword;

    //어드민 이름
    private String adminName;

    //어드민 주민번호
    private String adminResidentNum;;

    //어드민 이메일
    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String adminEmail;

    //어드민 주소
    private String adminAddress;

    //어드민 전화번호
    private String adminPhone;

    //어드민 직책
    private String adminRole;
//    private AdminRole adminRole;

    //어드민 Brand
    private String adminBrand;

    //어드민 입사일
    private LocalDate adminJoinDate;

    //어드민 생성일
    private LocalDateTime createDate;

    //어드민 수정일
    private LocalDateTime modifyDate;

    //어드민 승인처리
    private String adminActive;

    //어드민 직급
    private String adminPosition;

    // 어드민 사진
    private MultipartFile adminProfile;

    // 어드민 사진정보
    private String adminProfileMeta;

    // 어드민 FK 출력용
    private Long companyNum;
    private Long storeNum;

    private String companyName;
    private String storeName;

    public String getAdminRoleKorean() {
        return AdminRole.displayNameFromCode(adminRole);
    }
}
