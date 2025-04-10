package com.sixthsense.hexastay.service;

import com.sixthsense.hexastay.dto.CompanyDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;

public interface CompanyService {

    //center 등록
    public void centerInsert(CompanyDTO companyDTO) throws IOException;


}
