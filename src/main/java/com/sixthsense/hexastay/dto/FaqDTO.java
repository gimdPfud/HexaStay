package com.sixthsense.hexastay.dto;
import lombok.*;
import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FaqDTO {
    private Long faqNum; //번호
    private String faqTitle; //제목
    private String faqContent; //내용
    private LocalDateTime createDate; //등록일자
    private LocalDateTime modifyDate; //수정일자
    private Long memberNum; //관리자
}
