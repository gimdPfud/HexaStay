package com.sixthsense.hexastay.service;

import com.sixthsense.hexastay.dto.AdminDTO;
import com.sixthsense.hexastay.dto.CompanyDTO;
import com.sixthsense.hexastay.entity.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;

public interface CompanyService {

    //company 등록
    void companyInsert(CompanyDTO companyDTO) throws IOException ;

    //company 목록
    List<CompanyDTO> companyList ();
    Page<CompanyDTO> companyList (Pageable pageable);

    Page<CompanyDTO> companySearchList(String select, String choice, String keyword, Pageable pageable);

    //company 상세보기
    CompanyDTO companyRead(Long companyNum);

    //company 수정
    void companyModify(CompanyDTO companyDTO) throws IOException;

    //company 비활성화
    void deactivateCompany(Long companyNum);

    //companyStatus 비활성화를 활성화하기 (activateCompany)
    void activateCompany(Long companyNum);

    //company 소속 직원 조회
    List<AdminDTO> getCompanyAdmins (Long companyNum);

}
