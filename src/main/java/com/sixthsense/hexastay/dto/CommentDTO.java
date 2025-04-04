package com.sixthsense.hexastay.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CommentDTO {
    private Long commNum;
    private Integer commCategory;
    private String commContent;
    private Long commContentNum;

    private String createdAt;
    private String updatedAt;

    private Long adminName;
}
