package com.sixthsense.hexastay.controller;
import com.sixthsense.hexastay.dto.QaReplyDTO;
import com.sixthsense.hexastay.service.QaReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // (중요) 댓글은 비동기 처리니까 RestController 사용
@RequiredArgsConstructor
@RequestMapping("/qa/replies")
public class QaReplyController {

    private final QaReplyService qaReplyService;

    // 댓글 등록 (POST)
    @PostMapping("/add")
    public ResponseEntity<String> addReply(@RequestBody QaReplyDTO replyDTO) {
        qaReplyService.addReply(replyDTO);
        return ResponseEntity.ok("댓글이 등록되었습니다.");
    }

    // 특정 QA에 대한 댓글 목록 조회 (GET)
    @GetMapping("/{qaNum}")
    public ResponseEntity<List<QaReplyDTO>> getReplies(@PathVariable Long qaNum) {
        List<QaReplyDTO> replies = qaReplyService.searchReply(qaNum);
        return ResponseEntity.ok(replies);
    }
}

