package com.sixthsense.hexastay.service;
import com.sixthsense.hexastay.dto.FaqDTO;
import java.security.Principal;
import java.util.List;
public interface FaqService {
    // 등록
    public void faqInsert(FaqDTO faqDTO, Principal principal); // (수정)
    //목록
    public List<FaqDTO> faqList(); // (수정)
    // 보기
    public FaqDTO faqRead(Long faqNum); // (수정)
    // 수정
    public void faqModify(FaqDTO faqDTO); // (수정)
    // 삭제
    public void faqDelete(Long faqNum); // (수정)
}
