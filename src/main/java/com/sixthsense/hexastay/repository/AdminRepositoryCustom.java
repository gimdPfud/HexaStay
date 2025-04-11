package com.sixthsense.hexastay.repository;

import com.sixthsense.hexastay.entity.Admin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminRepositoryCustom {
    Page<Admin> listAdminSearch(String select, String choice, String keyword, Pageable pageable);
}
