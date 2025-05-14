package com.sixthsense.hexastay.service;

import com.sixthsense.hexastay.dto.NoticeDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.security.Principal;

public interface NoticeService {
    //등록
    public void noticeInsert(NoticeDTO noticeDTO);
    //목록
    public Page<NoticeDTO> noticeList(Pageable pageable, Principal principal , String type, String keyword);
    //읽기
    public NoticeDTO noticeRead(Long noticeNum);
    //수정
    public void noticeModify(NoticeDTO noticeDTO);
    //삭제
    public void noticeDelete(Long noticeNum);
}
