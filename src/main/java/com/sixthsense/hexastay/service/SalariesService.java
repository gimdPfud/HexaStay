package com.sixthsense.hexastay.service;

import com.sixthsense.hexastay.dto.AdminDTO;
import com.sixthsense.hexastay.dto.SalariesDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public interface SalariesService {

//    Page<AdminDTO> getSalariesInsertCompany(String email, Pageable pageable);
//    Page<AdminDTO> getSalariesInsertStore(String email, Pageable pageable);
    Page<SalariesDTO> getSalariesList(String email, Pageable pageable);
    
    // 월별 범위로 급여 목록 조회
    Page<SalariesDTO> getSalariesListByMonthRange(String email, YearMonth startMonth, YearMonth endMonth, Pageable pageable);

    Page<AdminDTO> getSalarAdminList(String email, Pageable pageable);

    void postSalaries(SalariesDTO salariesDTO);

    // 스토어 관련 메서드 추가
    Page<SalariesDTO> getStoreSalariesList(Long storeNum, Pageable pageable);
    Page<SalariesDTO> getStoreSalariesListByDateRange(Long storeNum, LocalDate startDate, LocalDate endDate, Pageable pageable);
    List<SalariesDTO> getAllStoreSalariesList(Long storeNum);
    List<SalariesDTO> getAllStoreSalariesListByDateRange(Long storeNum, LocalDate startDate, LocalDate endDate);
    void registerStoreSalaries(SalariesDTO salariesDTO);
    SalariesDTO getStoreSalaries(Long salariesNum);
    void modifyStoreSalaries(SalariesDTO salariesDTO);
    void removeStoreSalaries(Long salariesNum);
}
