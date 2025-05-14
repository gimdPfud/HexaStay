package com.sixthsense.hexastay.service;

import com.sixthsense.hexastay.dto.AdminDTO;
import com.sixthsense.hexastay.dto.CompanyDTO;
import com.sixthsense.hexastay.dto.StoreDTO;
import com.sixthsense.hexastay.entity.Admin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;

public interface AdminService {

    void insertAdmin(AdminDTO adminDTO) throws IOException;

    // 리스트 페이지용

    Page<AdminDTO> listAdmin(String email, Pageable pageable);
    public Page<AdminDTO> listAdminSearch(String email, String type, String keyword, Pageable pageable);

    //가입관련
    //사번값 자동 완성
    Admin createAdminEmployeeNum(Admin admin);

    // 가입자 대기 리스트
    List<AdminDTO> getWaitAdminList();

    //가입승인
    void setAdminActive(Long adminNum);

    AdminDTO adminRead(Long adminNum);

    void adminUpdate(AdminDTO adminDTO) throws IOException;

    void adminDelete(Long adminNum) throws IOException;

    AdminDTO adminFindEmail(String email);

    List<CompanyDTO> insertSelectList (Long centerNum, String adminChoice);

    List<StoreDTO> insertStoreList (Long companyNum);

    List<CompanyDTO> insertSelectCompany (String companyType);

    // 비밀번호 변경
    void updatePassword(String name, String employeeNum, String birth, 
                       String currentPassword, String newPassword);
}
