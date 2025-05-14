package com.sixthsense.hexastay.repository;

import com.sixthsense.hexastay.entity.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StoreRepositoryCustom {
    Page<Store> listStoreSearch(List<Long> companyNum, String searchType , String keyword, Pageable pageable, String... status);
    Page<Store> storeTypeSearch(Long companyNum, String storeType, String menuKeyword, Pageable pageable);
}
