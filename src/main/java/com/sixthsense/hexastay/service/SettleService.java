package com.sixthsense.hexastay.service;

import com.sixthsense.hexastay.dto.CompanyDTO;
import com.sixthsense.hexastay.entity.Company;

import java.util.ArrayList;
import java.util.List;

public interface SettleService {


    // 컴퍼니넘 소속 호텔 찾기
    List<CompanyDTO> getCompanyParent(Long companyNum);

}
