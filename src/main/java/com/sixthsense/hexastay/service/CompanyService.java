package com.sixthsense.hexastay.service;

import com.sixthsense.hexastay.dto.CompanyDTO;
import com.sixthsense.hexastay.entity.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;

public interface CompanyService {

    //center 등록
    void companyInsert(CompanyDTO companyDTO) throws IOException ;

    List<CompanyDTO> companyList ();
    Page<CompanyDTO> companyList (Pageable pageable);
    public Page<CompanyDTO> companySearchList(String select, String choice, String keyword);

}
