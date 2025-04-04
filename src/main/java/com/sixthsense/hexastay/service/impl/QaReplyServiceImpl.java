package com.sixthsense.hexastay.service.impl;

import com.sixthsense.hexastay.dto.QaReplyDTO;
import com.sixthsense.hexastay.entity.Qa;
import com.sixthsense.hexastay.entity.QaReply;
import com.sixthsense.hexastay.repository.MemberRepository;
import com.sixthsense.hexastay.repository.QaReplyRepository;
import com.sixthsense.hexastay.repository.QaRepository;
import com.sixthsense.hexastay.service.QaReplyService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
@Transactional
@RequiredArgsConstructor
public class QaReplyServiceImpl implements QaReplyService {

    private final QaReplyRepository replyRepository;
    private final QaRepository qaRepository;

    @Override
    public void addReply(QaReplyDTO replyDTO) {
        Qa qa = qaRepository.findById(replyDTO.getQaNum())
                .orElseThrow(() -> new RuntimeException("해당 QA가 존재하지 않습니다."));

        QaReply reply = QaReply.builder()
                .replyContent(replyDTO.getReplyContent()) // (추가)
                .replyWriter(replyDTO.getReplyWriter())   // (추가)
                .qa(qa)                                    // (추가)
                .build();

        replyRepository.save(reply);
    }

    @Override
    public List<QaReplyDTO> searchReply(Long qaNum) {
        return replyRepository.findByQa_QaNumOrderByCreateDateAsc(qaNum)
                .stream()
                .map(reply -> QaReplyDTO.builder()
                        .replyNum(reply.getReplyNum())
                        .replyContent(reply.getReplyContent())
                        .replyWriter(reply.getReplyWriter())
                        .createDate(reply.getCreateDate()) // (추가)
                        .modifyDate(reply.getModifyDate()) // (추가)
                        .qaNum(reply.getQa().getQaNum())
                        .build())
                .collect(Collectors.toList());
    }
}
