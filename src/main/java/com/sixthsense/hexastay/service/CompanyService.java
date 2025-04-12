package com.sixthsense.hexastay.service;

import com.sixthsense.hexastay.dto.CompanyDTO;
import com.sixthsense.hexastay.entity.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;

public interface CompanyService {

    //조직 등록
    void companyInsert(CompanyDTO companyDTO) throws IOException ;

    //조직 목록
    List<CompanyDTO> companyList ();
    Page<CompanyDTO> companyList (Pageable pageable);

    //조직 검색
    public Page<CompanyDTO> companySearchList(String select, String choice, String keyword, Pageable pageable);

    //조직 상세보기
    public CompanyDTO companyRead(Long companyNum);

    //조직 수정

    //조직 삭제


}
