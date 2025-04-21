package com.sixthsense.hexastay.repository;

import com.sixthsense.hexastay.entity.Erd;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ErdRepository extends JpaRepository<Erd,Long> {

    Page<Erd> findByCompany_CompanyNum(Long companyNum, Pageable pageable);
    Page<Erd> findByStore_StoreNum(Long storeNum, Pageable pageable);

}
