package com.sixthsense.hexastay.repository;

import com.sixthsense.hexastay.entity.QaReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface QaReplyRepository extends JpaRepository<QaReply, Long> {
    List<QaReply> findByQa_QaNumOrderByCreateDateAsc(Long qaNum);
}
