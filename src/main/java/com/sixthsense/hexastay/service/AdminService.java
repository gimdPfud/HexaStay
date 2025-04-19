package com.sixthsense.hexastay.service;

import com.sixthsense.hexastay.dto.*;
import com.sixthsense.hexastay.entity.Admin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;

import java.io.IOException;
import java.util.List;

public interface AdminService {

    void insertAdmin(AdminDTO adminDTO) throws IOException;

    Page<AdminDTO> list (Pageable pageable);


    // 리스트 검색용
    Page<AdminDTO> searchAdmins(String select, String choice, String keyword, Pageable pageable);

    //가입대기자 리스트
    List<AdminDTO> getWaitAdminList();

    //가입승인
    void setAdminActive(Long adminNum);

    AdminDTO adminRead(Long adminNum);

    void adminUpdate(AdminDTO adminDTO) throws IOException;

    void adminDelete(Long adminNum) throws IOException;

    AdminDTO adminFindEmail(String adminEmail);

    List<CompanyDTO> insertSelectList (Long centerNum, String adminChoice);

    List<StoreDTO> insertStoreList (Long companyNum);

    List<CompanyDTO> insertSelectCompany (String companyType);

    Page<CompanyDTO> getCompanyList(Pageable pageable);
}
