package com.sixthsense.hexastay.repository;

import com.sixthsense.hexastay.entity.Admin;
import com.sixthsense.hexastay.entity.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StoreRepositoryCustom {
    Page<Store> listStoreSearch(String status, Long companyNum,String searchType ,String keyword, Pageable pageable);
}
