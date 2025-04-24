package com.sixthsense.hexastay.service;

import com.sixthsense.hexastay.dto.AdminDTO;
import com.sixthsense.hexastay.dto.SalariesDTO;
import com.sixthsense.hexastay.entity.Salaries;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SalariesService {

//    Page<AdminDTO> getSalariesInsertCompany(String email, Pageable pageable);
//    Page<AdminDTO> getSalariesInsertStore(String email, Pageable pageable);
    Page<SalariesDTO> getSalariesList(String email, Pageable pageable);

    Page<AdminDTO> getSalarAdminList(String email, Pageable pageable);

    void postSalaries(SalariesDTO salariesDTO);
}
