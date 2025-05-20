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
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminInsertDTO {

    private List<CompanyDTO> companyList;
    private String adminRole;
    private boolean isReadOnly;
    private String fixedChoice;
    private Long fixedCompanyNum;
    private String fixedCompanyName;
    private Long fixedStoreNum;
    private String fixedStoreName;
    private Long fixedParentCompanyNum;
    private String fixedParentCompanyName;

    private AdminDTO adminDTO;


}
