package com.sixthsense.hexastay.service;
import com.sixthsense.hexastay.dto.FaqDTO;
import com.sixthsense.hexastay.entity.Admin;
import com.sixthsense.hexastay.repository.AdminRepository;

import java.security.Principal;
import java.util.List;
public interface FaqService {
    // 등록
    public void faqInsert(FaqDTO faqDTO, Principal principal); // (수정)
    //목록
    public List<FaqDTO> faqList(String category); // (수정)
    // 보기
    public FaqDTO faqRead(Long faqNum); // (수정)
    // 수정
    public void faqModify(FaqDTO faqDTO); // (수정)
    // 삭제
    public void faqDelete(Long faqNum); // (수정)

    public String AdminRole(Principal principal);
}
