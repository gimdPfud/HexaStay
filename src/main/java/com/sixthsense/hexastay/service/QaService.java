package com.sixthsense.hexastay.service;

import com.sixthsense.hexastay.dto.QaDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.security.Principal;

public interface QaService {
    //등록
    public void qaInsert(QaDTO qaDTO);
    //목록
    public Page<QaDTO> qaList(Pageable pageable, Principal principal , String type, String keyword);
    //읽기
    public QaDTO qaRead(Long qaNum);
    //답변
    public void qaAnswered(Long qaNum);
    //삭제
    public void qaDelete(Long qaNum);

}
