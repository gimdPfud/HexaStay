package com.sixthsense.hexastay.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@Entity
@Getter
@Setter
@Table(name = "uploadFile")

public class UploadFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fileNum;

    private String fileName;
    private String filePath;
    private String fileSize;
    private Integer fileCategory;
    private Long fileContentNum;

    private LocalDateTime uploadedAt;

}
