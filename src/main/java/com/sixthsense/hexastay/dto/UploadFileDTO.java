package com.sixthsense.hexastay.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UploadFileDTO {

    private Long fileNum;

    private String fileName;
    private String filePath;
    private String fileSize;
    private Integer fileCategory;
    private Long fileContentNum;

    private LocalDateTime uploadedAt;

}
