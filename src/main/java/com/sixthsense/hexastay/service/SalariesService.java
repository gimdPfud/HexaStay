package com.sixthsense.hexastay.service;

import com.sixthsense.hexastay.dto.AdminDTO;
import com.sixthsense.hexastay.dto.SalariesDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.YearMonth;

public interface SalariesService {

//    Page<AdminDTO> getSalariesInsertCompany(String email, Pageable pageable);
//    Page<AdminDTO> getSalariesInsertStore(String email, Pageable pageable);
    Page<SalariesDTO> getSalariesList(String email, Pageable pageable);
    
    // 월별 범위로 급여 목록 조회
    Page<SalariesDTO> getSalariesListByMonthRange(String email, YearMonth startMonth, YearMonth endMonth, Pageable pageable);

    Page<AdminDTO> getSalarAdminList(String email, Pageable pageable);

    void postSalaries(SalariesDTO salariesDTO);
}
