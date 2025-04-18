package com.sixthsense.hexastay.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErdDTO {

    private Long erdNum;

    private String erdName;

    private String erdSku;

    private String erdType;

    private Integer erdUnit;

    private String erdNote;

    private MultipartFile erdPicture;

    private String erdPictureMeta;


    private Long comapnyNum;
    private String comapnyName;

    private Long storeNum;
    private String storeName;

}
