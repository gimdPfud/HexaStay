package com.sixthsense.hexastay.dto;

import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QaReplyDTO {
    private Long replyNum;
    private String replyContent;
    private String replyWriter;
    private LocalDateTime createDate;
    private LocalDateTime modifyDate;
    private Long qaNum; // 어떤 질문의 댓글인지
}

