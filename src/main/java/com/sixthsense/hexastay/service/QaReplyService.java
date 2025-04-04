package com.sixthsense.hexastay.service;

import com.sixthsense.hexastay.dto.QaReplyDTO;

import java.util.List;

public interface QaReplyService {
    void addReply(QaReplyDTO replyDTO); // 댓글 등록
    List<QaReplyDTO> searchReply(Long qaNum); // 댓글 목록 조회
}