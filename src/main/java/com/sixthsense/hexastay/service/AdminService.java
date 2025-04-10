package com.sixthsense.hexastay.service;

import com.sixthsense.hexastay.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;

public interface AdminService {



    // 리스트 검색용
    Page<AdminDTO> getAdminSearch(String type, String keyword, Pageable pageable);


}
