package com.sixthsense.hexastay.repository;

import com.sixthsense.hexastay.entity.Salaries;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettleRepository extends JpaRepository <Salaries, Integer> {

    Page<Salaries> findByAdmin_AdminNum(Long adminNum, Pageable pageable);
    Page<Salaries> findByAdmin_Company_CompanyNum(Long companyNum, Pageable pageable);
    Page<Salaries> findByAdmin_Store_StoreNum(Long storeNum, Pageable pageable);
}
