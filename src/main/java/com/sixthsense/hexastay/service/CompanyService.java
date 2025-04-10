package com.sixthsense.hexastay.service;

import com.sixthsense.hexastay.dto.CenterDTO;
import com.sixthsense.hexastay.dto.CompanyDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;

public interface CompanyService {

    //center 등록
    public void centerInsert(CompanyDTO companyDTO) throws IOException;

    //center 목록
    public Page<CompanyDTO> companyList(Pageable pageable);

    //center 상세보기
    public CompanyDTO companyRead(Long companyNum);

    //center 수정
    public void companyModify(CompanyDTO companyDTO);

    //center 삭제
    public void companyDelete(Long companyNum);

    //가입용, 조회용
    public List<CompanyDTO> allCompanyList();

    //조직명으로 조회
    Page<CompanyDTO> companyName(String keyword, Pageable pageable);

    //브랜드로 조회
    Page<CompanyDTO> brandName(String keyword, Pageable pageable);

    //사업자등록번호로 조회
    Page<CompanyDTO> companyBusinessNum(String keyword, Pageable pageable);


}
